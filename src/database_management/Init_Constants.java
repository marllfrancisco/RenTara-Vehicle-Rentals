package database_management;

public class Init_Constants {
    private Init_Constants(){}

    public static final String CREATE_USERS_TABLE = """
            CREATE TABLE IF NOT EXISTS users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL,
                full_name TEXT NOT NULL,
                is_kyc_approved BOOLEAN NOT NULL DEFAULT FALSE)
            """;

    public static final String CREATE_VEHICLES_TABLE = """
            CREATE TABLE IF NOT EXISTS vehicles (
                vehicle_id INTEGER PRIMARY KEY AUTOINCREMENT,
                owner_id INTEGER NOT NULL,
                vehicle_type TEXT NOT NULL,
                brand_model TEXT NOT NULL,
                daily_rate DECIMAL NOT NULL,
                is_available BOOLEAN NOT NULL DEFAULT TRUE,
                FOREIGN KEY (owner_id) REFERENCES users(user_id))
            """;

    public static final String CREATE_BOOKINGS_TABLE = """
            CREATE TABLE IF NOT EXISTS bookings (
                booking_id INTEGER NOT NULL,
                guest_id INTEGER NOT NULL,
                vehicle_id INTEGER NOT NULL,
                days_rented INTEGER NOT NULL,
                total_fee DECIMAL NOT NULL,
                status TEXT NOT NULL,
                FOREIGN KEY (guest_id) REFERENCES users(user_id),
                FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id))
            """;

    public static final String CREATE_ADMIN = """
            INSERT INTO users (username, password, role, full_name, is_kyc_approved)
            VALUES ('admin', 'admin', 'Admin', 'admin user', TRUE)
            """;
}
