package menus;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import database_management.Database;
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
            System.out.println("User List");
            System.out.println("User List\nID         NAME                 APPROVED");
            for (int i = 0; i < users.size(); i++) {
                if (!users.get(i).isAdmin()) {
                    System.out.println(formatAString(users.get(i).getId(), users.get(i).getFullName(), users.get(i).isKycApproved()));
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
            System.out.println("User List\nID         NAME                 APPROVED");
            for (int i = 0; i < users.size(); i++) {
                System.out.println(formatAString(users.get(i).getId(), users.get(i).getFullName(), users.get(i).isKycApproved()));
            }

            System.out.print("Press ENTER to continue");
            scan.nextLine();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
