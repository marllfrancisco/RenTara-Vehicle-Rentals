package src.database_management;

public class Init_Constants {
    private Init_Constants(){}

    public static final String CREATE_USERS_TABLE = """
            CREATE TABLE IF NOT EXISTS users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                password TEXT NOT NULL,
                role TEXT NOT NULL,
                full_name TEXT NOT NULL,
                is_kyc_approved INTEGER NOT NULL DEFAULT 0)
            """;

    public static final String CREATE_VEHICLES_TABLE = """
            CREATE TABLE IF NOT EXISTS vehicles (
                vehicle_id INTEGER PRIMARY KEY AUTOINCREMENT,
                owner_id INTEGER NOT NULL,
                vehicle_type TEXT NOT NULL,
                brand_model TEXT NOT NULL,
                daily_rate DECIMAL NOT NULL,
                is_available INTEGER NOT NULL DEFAULT 1,
                FOREIGN KEY (owner_id) REFERENCES users(user_id))
            """;

    public static final String CREATE_BOOKINGS_TABLE = """
            CREATE TABLE IF NOT EXISTS bookings (
                booking_id INTEGER NOT NULL,
                guest_id INTEGER NOT NULL,
                vehicle_id INTEGER NOT NULL,
                days_rented INTEGER NOT NULL,
                total_fee DECIMAL NOT NULL,
                status STRING NOT NULL,
                FOREIGN KEY (guest_id) REFERENCES users(user_id),
                FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id))
            """;
}
