package menus;

import database_management.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    public String compare_input(String user, String pass) throws SQLException {

        try {
            PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            statement.setString(1, user);
            statement.setString(2, pass);

            ResultSet rs = statement.executeQuery();
            if (!rs.next()) {
                throw new SQLException();
            }
            else {
                return rs.getString(3);
            }
        }
        catch (SQLException e) {
            throw new SQLException();
        }
    }

    public String start(){


        int attempts = 3;

        while (attempts != 0) {

            System.out.print("\033[H\033[2J");

            String user = ask_info("username");
            String pass = ask_info("password");

            try {
                String value = compare_input(user, pass);
                if (value != null) return value;
            }
            catch (SQLException e) {
                e.printStackTrace();
                attempts -= 1;
            }
        }   
        scan.close();
        return "None";
    }
}