
/* CHANGES NEED TO IMPLEMENT:
 * 1. Username check
 * 		In both REGISTER and LOGIN, after entering username, DB should look for that 
 * 		username, if it exist, reject, make the user try another username
 * 
 * 2. Password should be in asterisk, well if this is only possible
 * 
 */



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
        System.out.print("Enter " + question + ": ");
        return scan.nextLine();
    }

    /**
     * Account Registration Function
     */
    private void register() {

        while(true) {
            System.out.print("\033[H\033[2J"); //Clear screen

            try {

                System.out.print("\nCreate Username: ");
                String username = scan.nextLine();
                System.out.print("Create Password: ");
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

            String username = ask_info("Username");
            String password = ask_info("Password");

            try {
                Optional<User> user = urep.findByUserAndPass(username, password);
                if (user.isEmpty()) throw new SQLException();
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

    public Optional<User> start() {
        while (true) { // keeps menu alive
            // menu always starts fresh
            System.out.print("\033[H\033[2J"); 
            System.out.flush(); // Ensure it clears immediately

            // Print the menu
            System.out.print(MenuStrings.LOGIN_STRING);
            String selected = scan.nextLine().trim(); // avoid accidental space errors

            // Handle the logic
            switch (selected) {
                case "1":
                    return login(); // Only exits the loop if login returns a user
                case "2":
                    register();
                    // After registering, we don't return, so it loops back to the menu
                    break; 
                case "3":
                    System.out.println("Exiting system...");
                    return Optional.empty();
                default:
                    // Handle invalid input
                    System.out.println("\n[Error] Invalid option: '" + selected+ "'");
                    System.out.println("Press Enter to continue...");
                    scan.nextLine(); // Pause so they can read the error
                    break; // restarts the loop
            }
        }
    }
}