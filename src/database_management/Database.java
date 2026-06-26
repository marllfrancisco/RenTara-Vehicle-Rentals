package src.database_management;
import java.sql.*;

public class Database {
    
    String url;
    Connection conn;
    
    public Database(String filename) {
        url = "jdbc:sqlite:data/" + filename;
        try_connect();
        init_tables();
    }

    public void try_connect() {
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Successfully Connected to Database");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void init_tables() {

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
}
