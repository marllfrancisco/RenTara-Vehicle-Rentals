package menus;

import java.sql.*;
import java.util.Scanner;
import database_management.Database;
import models.User;

public class CustomerMenu {

    private final Database db;
    private final Scanner scan;
    private final User currentUser;

    public CustomerMenu(Database db, Scanner scan, User currentUser) {this.db = db; this.scan = scan; this.currentUser = currentUser;}

    public void rentVehicle() {
        Connection conn = db.getConnection();
        String selectSql = "SELECT * FROM vehicles WHERE is_available = 1";
        
        try {
            System.out.print("\033[H\033[2J");
            System.out.println("\n=============================================");
            System.out.println("             AVAILABLE VEHICLES              ");
            System.out.println("=============================================");

            boolean hasVehicles = false;
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(selectSql)) {
                while (rs.next()) {
                    hasVehicles = true;
                    System.out.printf("%d - %s - %s - PHP %.2f\n",
                            rs.getInt("vehicle_id"),
                            rs.getString("vehicle_type"),
                            rs.getString("brand_model"),
                            rs.getDouble("daily_rate"));
                }
            }

            if (!hasVehicles) {
                System.out.println("No vehicles are currently available for rent.");
                System.out.println("\nPress ENTER to return...");
                scan.nextLine();
                return;
            }

            System.out.println("---------------------------------------------");
            System.out.print("Enter the Vehicle ID you want to rent: ");
            int vehicleId = scan.nextInt();
            System.out.print("Enter number of days to rent: ");
            int days = scan.nextInt();
            scan.nextLine(); // Clear scanner buffer

            // Verify vehicle exists and get its rate
            String checkSql = "SELECT daily_rate FROM vehicles WHERE vehicle_id = ? AND is_available = 1";
            double dailyRate = 0;
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, vehicleId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        dailyRate = rs.getDouble("daily_rate");
                    } else {
                        System.out.println("\n[Error] Invalid or unavailable Vehicle ID.");
                        System.out.println("Press ENTER to continue...");
                        scan.nextLine();
                        return;
                    }
                }
            }

            double totalFee = dailyRate * days;

            int nextBookingId = 1;
            String maxIdSql = "SELECT MAX(booking_id) FROM bookings";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(maxIdSql)) {
                if (rs.next()) {
                    nextBookingId = rs.getInt(1) + 1;
                }
            }

            // Insert booking record
            String bookingSql = "INSERT INTO bookings (booking_id, guest_id, vehicle_id, days_rented, total_fee, status) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(bookingSql)) {
                ps.setInt(1, nextBookingId);
                ps.setInt(2, currentUser.getUserId());
                ps.setInt(3, vehicleId);
                ps.setInt(4, days);
                ps.setDouble(5, totalFee);
                ps.setString(6, "Pending");
                ps.executeUpdate();
            }

            // Mark vehicle as unavailable
            String updateVehicleSql = "UPDATE vehicles SET is_available = 0 WHERE vehicle_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateVehicleSql)) {
                ps.setInt(1, vehicleId);
                ps.executeUpdate();
            }

            System.out.println("\n[Success] Rental request submitted successfully!");
            System.out.printf("Total Fee: PHP %.2f (Pending Approval)\n", totalFee);
            System.out.println("\nPress ENTER to continue...");
            scan.nextLine();

        } catch (SQLException e) {
            System.out.println("[Error] Database operation failed.");
            e.printStackTrace();
            scan.nextLine();
        }
    }
 
    public void addVehicle() {
        System.out.print("\033[H\033[2J");
        System.out.println("\n=============================================");
        System.out.println("            LIST YOUR VEHICLE                ");
        System.out.println("=============================================");
        
        System.out.print("Enter Vehicle Type  : ");
        String type = scan.nextLine().trim();
        
        System.out.print("Enter Brand & Model : ");
        String brandModel = scan.nextLine().trim();
        
        System.out.print("Enter Daily Rate    : ");
        double rate = scan.nextDouble();
        scan.nextLine(); // Clear scanner buffer

        Connection conn = db.getConnection();
        String insertSql = "INSERT INTO vehicles (owner_id, vehicle_type, brand_model, daily_rate, is_available) VALUES (?, ?, ?, ?, 1)";

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, currentUser.getUserId());
            ps.setString(2, type);
            ps.setString(3, brandModel);
            ps.setDouble(4, rate);
            ps.executeUpdate();

            System.out.println("\n[Success] Your vehicle has been listed for rent successfully!");
            System.out.println("Press ENTER to continue...");
            scan.nextLine();
        } catch (SQLException e) {
            System.out.println("[Error] Failed to list vehicle.");
            e.printStackTrace();
            scan.nextLine();
        }
    }

    public void returnRent() {
        Connection conn = db.getConnection();
        // Select active bookings belonging to the current user
        String selectSql = """
            SELECT b.booking_id, v.brand_model, b.total_fee, v.vehicle_id 
            FROM bookings b 
            JOIN vehicles v ON b.vehicle_id = v.vehicle_id 
            WHERE b.guest_id = ? AND b.status = 'Approved'
        """;

        try {
            System.out.print("\033[H\033[2J");
            System.out.println("\n=============================================");
            System.out.println("             YOUR ACTIVE RENTALS             ");
            System.out.println("=============================================");

            boolean hasRentals = false;
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setInt(1, currentUser.getUserId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        hasRentals = true;
                        System.out.printf("%d - %s - PHP %.2f\n",
                                rs.getInt("booking_id"),
                                rs.getString("brand_model"),
                                rs.getDouble("total_fee"));
                    }
                }
            }

            if (!hasRentals) {
                System.out.println("You have no active approved rentals to return.");
                System.out.println("\nPress ENTER to return...");
                scan.nextLine();
                return;
            }

            System.out.println("---------------------------------------------");
            System.out.print("Enter the Booking ID of the vehicle you want to return: ");
            int bookingId = scan.nextInt();
            scan.nextLine(); // Clear scanner buffer

            // Verify booking exists, belongs to user, and fetch vehicle_id
            String verifySql = "SELECT vehicle_id FROM bookings WHERE booking_id = ? AND guest_id = ? AND status = 'Approved'";
            int vehicleId = -1;
            try (PreparedStatement ps = conn.prepareStatement(verifySql)) {
                ps.setInt(1, bookingId);
                ps.setInt(2, currentUser.getUserId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        vehicleId = rs.getInt("vehicle_id");
                    } else {
                        System.out.println("\n[Error] Invalid Booking ID or rental is not active.");
                        System.out.println("Press ENTER to continue...");
                        scan.nextLine();
                        return;
                    }
                }
            }

            // Update booking status to Completed
            String updateBookingSql = "UPDATE bookings SET status = 'Completed' WHERE booking_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateBookingSql)) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }

            // Mark the vehicle as available again
            String updateVehicleSql = "UPDATE vehicles SET is_available = 1 WHERE vehicle_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateVehicleSql)) {
                ps.setInt(1, vehicleId);
                ps.executeUpdate();
            }

            System.out.println("\n[Success] Vehicle returned successfully! Waiting for data synchronization.");
            System.out.println("Press ENTER to continue...");
            scan.nextLine();

        } catch (SQLException e) {
            System.out.println("\n[Error] Database operation failed during vehicle return.");
            e.printStackTrace();
            scan.nextLine();
        }
    }

    public void unlistVehicle() {
        Connection conn = db.getConnection();
        String selectSql = "SELECT * FROM vehicles WHERE owner_id = ?";

        try {
            System.out.print("\033[H\033[2J");
            System.out.println("\n=============================================");
            System.out.println("              YOUR LISTED VEHICLES           ");
            System.out.println("=============================================");

            boolean hasVehicles = false;
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setInt(1, currentUser.getUserId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        hasVehicles = true;
                        String status = rs.getInt("is_available") == 1 ? "Available" : "Rented";
                        System.out.printf("%d - %s - %s - %s\n",
                                rs.getInt("vehicle_id"),
                                rs.getString("vehicle_type"),
                                rs.getString("brand_model"),
                                status);
                    }
                }
            }

            if (!hasVehicles) {
                System.out.println("You do not have any registered vehicles listed.");
                System.out.println("\nPress ENTER to return...");
                scan.nextLine();
                return;
            }

            System.out.println("---------------------------------------------");
            System.out.print("Enter the Vehicle ID you want to unlist/delete: ");
            int vehicleId = scan.nextInt();
            scan.nextLine(); // Clear scanner buffer

            // Verify ownership before deleting
            String verifySql = "SELECT vehicle_id FROM vehicles WHERE vehicle_id = ? AND owner_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(verifySql)) {
                ps.setInt(1, vehicleId);
                ps.setInt(2, currentUser.getUserId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("\n[Error] Invalid Vehicle ID or you do not own this vehicle.");
                        System.out.println("Press ENTER to continue...");
                        scan.nextLine();
                        return;
                    }
                }
            }

            // Remove the vehicle from the database
            String deleteSql = "DELETE FROM vehicles WHERE vehicle_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, vehicleId);
                ps.executeUpdate();
            }

            System.out.println("\n[Success] Your vehicle has been removed from the renting pool.");
            System.out.println("Press ENTER to continue...");
            scan.nextLine();

        } catch (SQLException e) {
            System.out.println("\n[Error] Database operation failed during unlisting.");
            e.printStackTrace();
            scan.nextLine();
        }
    }

}
