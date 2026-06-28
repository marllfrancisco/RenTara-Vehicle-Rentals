package menus;

import java.util.Scanner;

import database_management.Database;
import models.Vehicle;

public class MainMenu {
    Database db;

    /**
     * 
     * @param db The main Database to be used
     */
    public MainMenu(Database db){
        this.db = db;
    }

    /**
     * Admin menu entry point
     */
    public void admin_start(){
        Scanner scan = new Scanner(System.in);
        AdminMenu menu = new AdminMenu(db, scan);

        while(true) {
            System.out.print("\033[H\033[2J");
            
            System.out.print(MenuStrings.ADMIN_STRING);
            String ans = scan.nextLine();
            switch (ans) {
                case "1":
                    menu.approveKYC();
                    break;
                case "2":
                    menu.viewAll();
                    break;
                case "3":
                    menu.viewRevenueReport();
                    break;
                case "4":
                	System.out.println("\nLogged out.");
                    return; // Exits the admin_start method smh
                default:
                	// Handle invalid input
                    System.out.println("\n[Error] Invalid option: '" + ans + "'");
                    System.out.println("\nPress Enter to continue...");
                    scan.nextLine(); // Pause so they can read the error
                    break; // restarts the loop
            }

        }
    }

    /**
     * Customer menu entry point
     */
    public void customer_start(models.User user) {
        Scanner scan = new Scanner(System.in);
        CustomerMenu menu = new CustomerMenu(db, scan, user);

        while(true) {
            System.out.print("\033[H\033[2J");
            
            System.out.print(MenuStrings.CUSTOMER_STRING);
            String ans = scan.nextLine();
            switch (ans) {
                case "1":
                    menu.rentVehicle();
                    break;
                case "2":
                    menu.addVehicle();
                    break;
                case "3":   
                    menu.returnRent();
                    break;
                case "4":
                    menu.unlistVehicle();
                    break;
                case "5":   
                    scan.close();
                    return;
                default:
                    break;
            }

        }
    }
    
    public void viewRecordsModule() {	
    	// Applied here is the polymorphism
    	Vehicle[] vehicleList = null;
    }
}
