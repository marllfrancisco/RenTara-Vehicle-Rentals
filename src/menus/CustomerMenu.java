package menus;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import database_management.Database;
import models.Booking;
import models.User;
import models.Vehicle;
import models.VehicleRepository;
import models.BookingRepository;

public class CustomerMenu {

    private static final String BookingRepository = null;
    private final Database db;
    private final Scanner scan;
    private final User currentUser;

    public CustomerMenu(Database db, Scanner scan, User currentUser) {this.db = db; this.scan = scan; this.currentUser = currentUser;}

    public void rentVehicle() {
        try {
            System.out.print("\033[H\033[2J");
            System.out.println("\n=============================================");
            System.out.println("             AVAILABLE VEHICLES              ");
            System.out.println("=============================================");

            VehicleRepository vehicleRepo = new VehicleRepository(db.getConnection());
            BookingRepository bookingRepo = new BookingRepository(db.getConnection());
            List<Vehicle> vehicles = vehicleRepo.findAllAvailable();

            for (int i = 0; i < vehicles.size(); i++) {
                Vehicle curr = vehicles.get(i);
                System.out.printf("%d -- %S -- %S -- Php %.2f\n", curr.getVehicleId(), curr.getVehicleType(), curr.getBrandModel(), curr.getDailyRate());
            }

            if (vehicles.isEmpty()) {
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

            Optional<Vehicle> result = vehicleRepo.findById(vehicleId);
            Vehicle vehicle = new Vehicle();

            if (result.isPresent()) vehicle = result.get();
            double totalFee =vehicle.getDailyRate() * days;

            Booking booking = new Booking(currentUser.getUserId(), vehicleId, days, totalFee);
            bookingRepo.insert(booking);

            vehicle.setIsAvailable(false);
            vehicleRepo.update(vehicle);

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

        if (!currentUser.isKycApproved()) return; // pls add error notif later

        System.out.print("\033[H\033[2J");
        System.out.println("\n=============================================");
        System.out.println(  "            LIST YOUR VEHICLE                ");
        System.out.println(  "=============================================");
        
        System.out.print("Enter Vehicle Type  : ");
        String type = scan.nextLine().trim();
        
        System.out.print("Enter Brand & Model : ");
        String brandModel = scan.nextLine().trim();
        
        System.out.print("Enter Daily Rate    : ");
        double rate = scan.nextDouble();
        scan.nextLine(); // Clear scanner buffer

        VehicleRepository vehicleRepo = new VehicleRepository(db.getConnection());
        Vehicle new_vehicle = new Vehicle(currentUser.getUserId(), type, brandModel, rate);

        try {
            vehicleRepo.insert(new_vehicle);
        }
        catch (SQLException e){
            e.printStackTrace();
            return;
        }
        finally {
            scan.nextLine();
        }
    }

    public void returnRent() {

        VehicleRepository vehicleRepo = new VehicleRepository(db.getConnection());
        BookingRepository bookingRepo = new BookingRepository(db.getConnection());

        try {
            System.out.print("\033[H\033[2J");
            System.out.println("\n=============================================");
            System.out.println(  "             YOUR ACTIVE RENTALS             ");
            System.out.println(  "=============================================");

            List<Booking> bookings = bookingRepo.findAllRentsOfUser(currentUser.getUserId());
            for (int i = 0; i < bookings.size(); i++) {
                Booking curr = bookings.get(i);
                System.out.printf("%d -- %S -- Php %.2f\n", curr.getBookingId(), curr.getVehicleModel(), curr.getTotalFee());
            }

            if (bookings.isEmpty()) {
                System.out.println("You have no active approved rentals to return.");
                System.out.println("\nPress ENTER to return...");
                scan.nextLine();
                return;
            }

            System.out.println("---------------------------------------------");
            System.out.print("Enter the Booking ID of the vehicle you want to return: ");
            int bookingId = scan.nextInt();
            scan.nextLine(); // Clear scanner buffer

            Optional<Booking> newBooking = bookingRepo.findById(bookingId);
            Booking booking = new Booking();
            if (newBooking.isPresent()) booking = newBooking.get();

            Optional<Vehicle> newVehicle = vehicleRepo.findById(booking.getVehicleId());
            Vehicle vehicle = new Vehicle();
            if (newVehicle.isPresent()) vehicle = newVehicle.get();

            booking.setStatus("Paid");
            bookingRepo.update(booking);
            vehicle.setIsAvailable(true);
            vehicleRepo.update(vehicle);

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

        try {
            System.out.print("\033[H\033[2J");
            System.out.println("\n=============================================");
            System.out.println("              YOUR LISTED VEHICLES           ");
            System.out.println("=============================================");


            VehicleRepository repo = new VehicleRepository(db.getConnection());
            List<Vehicle> vehicles = repo.findAllAvailableOfOwner(currentUser.getUserId());
            for (int i = 0; i < vehicles.size(); i++) {
                Vehicle curr = vehicles.get(i);
                String isAvailable = curr.isAvailable() ? "Available" : "Rented";
                System.out.printf("%d -- %S -- %S -- %S\n", curr.getVehicleId(), curr.getVehicleType(), curr.getBrandModel(), isAvailable);
            }

            if (vehicles.isEmpty()) {
                System.out.println("You do not have any registered vehicles listed.");
                System.out.println("\nPress ENTER to return...");
                scan.nextLine();
                return;
            }

            System.out.println("---------------------------------------------");
            System.out.print("Enter the Vehicle ID you want to unlist/delete: ");
            int vehicleId = scan.nextInt();
            scan.nextLine(); // Clear scanner buffer

            try {
                int a = repo.delete(currentUser.getUserId(), vehicleId);
                if (a == 0) throw new SQLException();
            }
            catch (SQLException e) {
                System.out.println("SQL ERROR: Deleting vehicle failed\nPress ENTER to continue...");
                scan.nextLine();
                return;
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
