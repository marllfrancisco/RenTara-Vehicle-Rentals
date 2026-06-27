package database_management;
import java.sql.*;

public class Database {
    
    private String url;
    private Connection conn;
    
    public Database(String filename) {
    	try {
            // Establish the connection
            String url = "jdbc:sqlite:data/" + filename;
            this.conn = DriverManager.getConnection(url);
            
            // Apply the PRAGMA fixes to prevent [SQLITE_BUSY]
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA journal_mode = WAL;");
                stmt.execute("PRAGMA busy_timeout = 5000;");
            }
            
            System.out.println("Successfully Connected to Database");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    	
        initializeTables();
        initializeAdmin();
    }

    public Connection getConnection() { return this.conn; }
    public void setConnection () {
        try {
            conn = DriverManager.getConnection(url);
            System.out.println("Successfully Connected to Database");
            
            // Enable Write-Ahead Logging (WAL)
            // This allows multiple readers and one writer to work simultaneously 
            // without locking the file.
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA journal_mode = WAL;");
            }

            // Set Busy Timeout
            // If the database is locked, SQLite will wait 5 seconds
            // for it to unlock before giving up.
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA busy_timeout = 5000;");
            }
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
