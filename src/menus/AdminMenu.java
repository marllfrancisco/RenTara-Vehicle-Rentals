package menus;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import database_management.Database;
import models.BookingRepository;
import models.User;
import models.UserRepository;

public class AdminMenu {

    Database db;
    Scanner scan; 
    public AdminMenu(Database db, Scanner scan) { this.db = db; this.scan = scan;}


    public void approveKYC() {

        UserRepository urepo = new UserRepository(db.getConnection());

        try {
            List<User> users = urepo.findAll();

            System.out.print("\033[H\033[2J");
            
            // %-5s  = String, left-aligned (-), 5 characters wide
            // %-20s = String, left-aligned (-), 20 characters wide
            // %-10s = String, left-aligned (-), 10 characters wide
            // %n    = Platform-independent newline (like \n)
            String rowTemplate = "%-5s %-10s %-15s %-5s%n";
            
            System.out.println("\n\t   ===== User List =====");
            System.out.printf(rowTemplate, "ID", "USERNAME", "NAME", "APPROVED");
            System.out.println("-------------------------------------------");
            
            
            for (int i = 0; i < users.size(); i++) {
                if (!users.get(i).isAdmin()) {
                    System.out.printf(rowTemplate,
                    		users.get(i).getId(), 
                    		users.get(i).getUsername(),
                    		users.get(i).getFullName(), 
                    		users.get(i).isKycApproved());
                }
            }

            System.out.println();
            System.out.print("Enter Username or ID: ");
            String input = scan.nextLine().trim(); 
            
            if (input.isEmpty()) {
                System.out.println("\n[Canceled] Proceeding to Main Menu...");
                return; // Exit - returns to menu loop
            }

            // single placeholder 
            Optional<User> foundUser = Optional.empty();

            // Check if digits
            if (input.matches("\\d+")) {
                int id = Integer.parseInt(input); // String to int 
                foundUser = urepo.findById(id);   
            } else {
                // If letters = username
                foundUser = urepo.findByUsername(input);
            }
            
            
            // Now process the single result variable
            if (foundUser.isPresent()) {
            	User user = foundUser.get();
            	
                if (!user.isAdmin()) { // Prevent admin tampering
                    user.switchKycApproved();
                    urepo.update(user);
                    System.out.println("\n[Success] User " + user.getFullName() + " (" + user.getUsername() + ") KYC approved.");
                } else {
                    System.out.println("\n[Error] Cannot modify Admin accounts.");
                }
            } else {
                System.out.println("\n[Error] No user found matching: '" + input + "'");
            }

            System.out.println("\nPress ENTER to continue");
            scan.nextLine();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View all Users
    public void viewAll() {

        UserRepository urepo = new UserRepository(db.getConnection());

        try {
            List<User> users = urepo.findAll();

            System.out.print("\033[H\033[2J");
            
            String rowTemplate = "%-5s %-10s %-15s %-5s%n";
            
            System.out.println("\n\t   ===== User List =====");
            System.out.printf(rowTemplate, "ID", "USERNAME", "NAME", "APPROVED");
            System.out.println("-------------------------------------------");
            
            
            for (int i = 0; i < users.size(); i++) {
                if (!users.get(i).isAdmin()) {
                    System.out.printf(rowTemplate,
                    		users.get(i).getId(), 
                    		users.get(i).getUsername(),
                    		users.get(i).getFullName(), 
                    		users.get(i).isKycApproved());
                }
            }

            System.out.println("\nPress ENTER to continue");
            scan.nextLine();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewRevenueReport() {
        BookingRepository repo = new BookingRepository(db.getConnection());
        try {
        	System.out.println();
        	System.out.println("-------------------------------------------");
            repo.printTotal();
            repo.printPending();
            System.out.println("-------------------------------------------");
            repo.print();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        System.out.println("\nPress ENTER to continue");
        scan.nextLine();
    }

}
