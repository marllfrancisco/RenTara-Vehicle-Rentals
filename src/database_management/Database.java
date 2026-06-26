package src.database_management;
import java.sql.*;

public class Database {
    
    String url;
    Connection conn;
    
    public Database(String filename) {
        url = "jdbc:sqlite:" + filename;
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
        String account_statement = """
                CREATE TABLE IF NOT EXISTS 
                """;
    }
}
