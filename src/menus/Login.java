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

    public String ask_info(String question) {
        System.out.print("Enter " + question + ":");
        return scan.nextLine();
    }

    public ResultSet compare_input(String user, String pass) throws SQLException {

        try {
            PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            statement.setString(1, user);
            statement.setString(2, pass);

            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                throw new SQLException();
            }
            else {
                return rs;
            }
        }
        catch (SQLException e) {
            throw new SQLException();
        }
    }

    public Optional<User> start(){

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
}