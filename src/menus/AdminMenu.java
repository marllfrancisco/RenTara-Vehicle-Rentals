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

    private String formatAString(Integer ID, String username, Boolean approved) {
        String idFormatted = String.format("%-5s", ID);
        String nameFormatted = String.format("%-30s", username);
        String approvedFormatted = String.format("%-10s", approved);

        return idFormatted + nameFormatted + approvedFormatted;
    }


    public void approveKYC() {

        UserRepository urepo = new UserRepository(db.getConnection());

        try {
            List<User> users = urepo.findAll();

            System.out.print("\033[H\033[2J");
            
            // %-5s  = String, left-aligned (-), 5 characters wide
            // %-20s = String, left-aligned (-), 20 characters wide
            // %-10s = String, left-aligned (-), 10 characters wide
            // %n    = Platform-independent newline (like \n)
            String rowTemplate = "%-5s %-20s %-10s%n";
            
            System.out.println("\n\t\t===== User List =====");
            System.out.printf(rowTemplate, "ID", "NAME", "APPROVED");
            System.out.println("----------------------------------------");
            
            
            for (int i = 0; i < users.size(); i++) {
                if (!users.get(i).isAdmin()) {
                    System.out.printf(rowTemplate,
                    		users.get(i).getId(), 
                    		users.get(i).getFullName(), 
                    		users.get(i).isKycApproved());
                }
            }

            System.out.print("Enter ID: ");
            int ID = scan.nextInt();
            
            Optional<User> search = urepo.findById(ID);
            if (search.isPresent()) {
                User user = search.get();
                if (!user.isAdmin()){ // Prevent admin tampering
                    user.switchKycApproved();
                    urepo.update(user);
                }
            }
            System.out.println("Press ENTER to continue");
            scan.nextLine();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewAll() {

        UserRepository urepo = new UserRepository(db.getConnection());

        try {
            List<User> users = urepo.findAll();

            System.out.print("\033[H\033[2J");
            
            String rowTemplate = "%-5s %-15s %-10s%n";
            
            System.out.println("\n\t    ===== User List =====");
            System.out.printf(rowTemplate, "ID", "NAME", "APPROVED");
            System.out.println("----------------------------------------");
            
            for (int i = 0; i < users.size(); i++) {
                System.out.printf(rowTemplate,
                		users.get(i).getId(), 
                		users.get(i).getFullName(), 
                		users.get(i).isKycApproved());
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
            repo.printTotal();
            repo.print();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        scan.nextLine();
    }

}
