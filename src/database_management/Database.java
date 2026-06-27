package database_management;
import java.sql.*;

public class Database {
    
    private String url;
    private Connection conn;
    
    public Database(String filename) {
        url = "jdbc:sqlite:data/" + filename;
        setConnection();
        initializeTables();
        initializeAdmin();
    }

    public Connection getConnection() { return conn; }
    public void setConnection() {
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Successfully Connected to Database");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initializeTables() {

        try {
            Statement statement = conn.createStatement();
            statement.execute(Init_Constants.CREATE_USERS_TABLE);
            statement.execute(Init_Constants.CREATE_VEHICLES_TABLE);
            statement.execute(Init_Constants.CREATE_BOOKINGS_TABLE);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initializeAdmin() {
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM users");
            if (rs.next()) { System.out.println("Skipping Account Creation..."); }
            else statement.executeUpdate(Init_Constants.CREATE_ADMIN); 
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
