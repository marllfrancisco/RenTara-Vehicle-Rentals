package menus;

import database_management.Database;
import models.User;
import models.UserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

public class Login {

    Database db;
    Scanner scan = new Scanner(System.in);

    public Login(Database db){
        this.db = db;
    }

    private String ask_info(String question) {
        System.out.print("Enter " + question + ":");
        return scan.nextLine();
    }

    /**
     * Account Registration Function
     */
    private void register() {

        while(true) {
            System.out.print("\033[H\033[2J"); //Clear screen

            try {

                System.out.print("Username: ");
                String username = scan.nextLine();
                System.out.print("Password: ");
                String password = scan.nextLine();
                System.out.print("Full name: ");
                String fullName = scan.nextLine();

                // Create new User with corresponding information
                User new_user = new User(username, password, "Customer", fullName);

                // Insert to repository via *insert()*
                UserRepository urepo = new UserRepository(db.getConnection());
                urepo.insert(new_user);
                return;
            }
            catch (Exception e) { e.printStackTrace(); }
        }

    }

    /**
     * Login actual function
     * <p>
     * Gets user input with 3 allowed incorrect attempts
     * 
     * @return User | null
     */
    private Optional<User> login(){
        
        UserRepository urep = new UserRepository(db.getConnection());
        int attempts = 3;

        while (attempts != 0) {

            System.out.print("\033[H\033[2J");

            String username = ask_info("username");
            String password = ask_info("password");

            try {
                Optional<User> user = urep.findByUserAndPass(username, password);
                return user;
            }
            catch (SQLException e) {
                e.printStackTrace();
                attempts -= 1;
            }
        }   
        scan.close();
        return Optional.empty();
    }

    /**
     * Login Menu Entry Point
     * <p>
     * Returns user if it's a correct login, 
     * if registered or wrong login, returns null
     * 
     * @return User | null
     */
    public Optional<User> start(){

        System.out.print(MenuStrings.LOGIN_STRING);
        String selected = scan.nextLine();
        
        switch (selected) {
            case "1":
                return login();
            case "2":
                register();
                return Optional.empty();
            case "3":
                return Optional.empty();
            default:
                return Optional.empty();
        }


    }
}