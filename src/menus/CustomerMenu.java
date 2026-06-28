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

    //private static final String BookingRepository = null;
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

            for (Vehicle curr : vehicles) {
                System.out.printf("%d -- %S -- %S -- Php %.2f\n", curr.getVehicleId(), curr.getVehicleType(), 
                													curr.getBrandModel(), curr.getDailyRate());
            }

            if (vehicles.isEmpty()) {
                System.out.println("No vehicles are currently available.");
                System.out.println("\nPress ENTER to return.");
                scan.nextLine();
                return;
            }

            System.out.println("---------------------------------------------");
            System.out.print("Enter Vehicle ID to rent: ");
            String idInput = scan.nextLine().trim();

            if (idInput.isEmpty()) {
                System.out.println("\n[Canceled] Proceeding to Main Menu...");
                return;
            }
            
            //validating input
            int vehicleId;
            try {
                vehicleId = Integer.parseInt(idInput);
            } catch (NumberFormatException e) {
                System.out.println("\n[Error] Invalid ID. Must be a number. \nPress ENTER to continue.");
                scan.nextLine();
                return;
            }
            
            //validating DB record
            Optional<Vehicle> result = vehicleRepo.findById(vehicleId);
            if (result.isEmpty()) {
                System.out.println("\n[Error] Vehicle ID not found. \nPress ENTER to continue...");
                scan.nextLine();
                return;
            }
            Vehicle vehicle = result.get();
            
            // PREVENT RENTING THEIR OWN VEHICLE
            if (vehicle.getOwnerId() == currentUser.getUserId()) {
                System.out.println("\n[Error] You cannot rent a vehicle you own.");
                System.out.println("Press ENTER to continue.");
                scan.nextLine();
                return;
            }
            
            System.out.print("Enter how many days to rent: ");
            String daysInput = scan.nextLine().trim();
            
            
            if (daysInput.isEmpty()) {
                System.out.println("\n[Canceled] Proceeding to Main Menu...");
                return;
            }

            int days;
            try {
                days = Integer.parseInt(daysInput);
            } catch (NumberFormatException e) {
                System.out.println("\n[Error] Invalid number of days. \nPress ENTER to continue.");
                scan.nextLine();
                return;
            }

            
            // TRANSACTION:
            double totalFee =vehicle.getDailyRate() * days;
            Booking booking = new Booking(currentUser.getUserId(), vehicleId, days, totalFee);
            
            bookingRepo.insert(booking);
            vehicle.setIsAvailable(false); // Now rented
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
        if (!currentUser.isKycApproved()) {
            System.out.println("\n[Error] Listing Vehicles feature is locked.");
            System.out.println("Pending KYC approval. Pls be patient.");
            System.out.println("Press ENTER to return.");
            scan.nextLine();
            return;
        }

        System.out.print("\033[H\033[2J");
        System.out.println("\n=============================================");
        System.out.println(  "            LIST YOUR VEHICLE                ");
        System.out.println(  "=============================================");
        
        System.out.print("Enter Vehicle Type  : ");
        String type = scan.nextLine().trim();
        if (type.isEmpty()) {
        	System.out.println("\n[Canceled] Proceeding to Main Menu..."); return;
        }
        	
        System.out.print("Enter Brand & Model : ");
        String brandModel = scan.nextLine().trim();
        if (brandModel.isEmpty()) {
        	System.out.println("\n[Canceled] Proceeding to Main Menu..."); return;
        }
        
        System.out.print("Enter Daily Rate    : ");
        String rateInput = scan.nextLine().trim();
        if (rateInput.isEmpty()) {
        	System.out.println("\n[Canceled] Proceeding to Main Menu..."); return;
        }
        
        
        double rate;
        try {
            rate = Double.parseDouble(rateInput);
        } catch (NumberFormatException e) {
            System.out.println("\n[Error] Invalid rate format.");
            System.out.println("Press ENTER to continue...");
            scan.nextLine();
            return;
        }

        VehicleRepository vehicleRepo = new VehicleRepository(db.getConnection());
        Vehicle new_vehicle = new Vehicle(currentUser.getUserId(), type, brandModel, rate);

        try {
            vehicleRepo.insert(new_vehicle);
            System.out.println("\n[Success] Vehicle successfully listed!");
        } catch (SQLException e) {
            System.out.println("\n[Error] Database operation failed.");
            e.printStackTrace();
        } finally {
            System.out.println("\nPress ENTER to continue.");
            scan.nextLine();
        }
    }

    public void returnRent() {

    	try {
            System.out.print("\033[H\033[2J");
            System.out.println("\n=============================================");
            System.out.println(  "             YOUR ACTIVE RENTALS             ");
            System.out.println(  "=============================================");

            VehicleRepository vehicleRepo = new VehicleRepository(db.getConnection());
            BookingRepository bookingRepo = new BookingRepository(db.getConnection());

            List<Booking> bookings = bookingRepo.findAllRentsOfUser(currentUser.getUserId());
            
            for (Booking curr : bookings) {
                System.out.printf("%d -- %s -- Php %.2f\n", 
                    curr.getBookingId(), curr.getVehicleModel(), curr.getTotalFee());
            }

            if (bookings.isEmpty()) {
                System.out.println("You have no active rentals to return.");
                System.out.println("\nPress ENTER to return...");
                scan.nextLine();
                return;
            }

            System.out.println("---------------------------------------------");
            System.out.print("Enter Booking ID to return: ");
            String idInput = scan.nextLine().trim();
            
            if (idInput.isEmpty()) {
                System.out.println("\n[Canceled] Proceeding to Main Menu...");
                return;
            }

            int bookingId;
            try {
                bookingId = Integer.parseInt(idInput);
            } catch (NumberFormatException e) {
                System.out.println("\n[Error] Invalid ID format. \nPress ENTER to continue.");
                scan.nextLine();
                return;
            }
            
            // Safe DB unwarapping 
            Optional<Booking> targetBooking = bookingRepo.findById(bookingId);
            if (targetBooking.isEmpty()) {
                System.out.println("\n[Error] Booking not found. Press ENTER to continue.");
                scan.nextLine();
                return;
            }
            Booking booking = targetBooking.get();
            
            Optional<Vehicle> targetVehicle = vehicleRepo.findById(booking.getVehicleId());
            if (targetVehicle.isEmpty()) {
                System.out.println("\n[Error] Vehicle not found in database.");
                scan.nextLine();
                return;
            }
            Vehicle vehicle = targetVehicle.get();
            
            // Process Return
            booking.setStatus("Paid");
            bookingRepo.update(booking);
            
            vehicle.setIsAvailable(true);
            vehicleRepo.update(vehicle);

            System.out.println("\n[Success] Vehicle returned successfully! "
            		+ "\nWaiting for data synchronization.");
            System.out.println("\nPress ENTER to continue.");
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
                System.out.printf("%d -- %s -- %s -- %s\n", curr.getVehicleId(), curr.getVehicleType(), curr.getBrandModel(), isAvailable);
            }

            if (vehicles.isEmpty()) {
                System.out.println("You do not have any registered vehicles listed.");
                System.out.println("\nPress ENTER to return.");
                scan.nextLine();
                return;
            }

            System.out.println("---------------------------------------------");
            System.out.print("Enter Vehicle ID to unlist/delete: ");
            String idInput = scan.nextLine().trim();
            if (idInput.isEmpty()) return;

            int vehicleId;
            try {
                vehicleId = Integer.parseInt(idInput);
            } catch (NumberFormatException e) {
                System.out.println("\n[Error] Invalid ID format.\nPress ENTER to continue.");
                scan.nextLine();
                return;
            }
            
            int rowsDeleted = repo.delete(currentUser.getUserId(), vehicleId);
            if (rowsDeleted == 0) {
                System.out.println("\n[Error] Failed to delete. "
                		+ "\nEither incorrect Vehicle ID or you do not own it.");
                
            } else {
                System.out.println("\n[Success] Your vehicle has been removed from the renting pool.");
            }
            
            System.out.println("Press ENTER to continue.");
            scan.nextLine();

        } catch (SQLException e) {
            System.out.println("\n[Error] Database operation failed during unlisting.");
            e.printStackTrace();
            System.out.println("Press ENTER to continue.");
            scan.nextLine();
        }
    }

}
