package menus;

import java.util.Scanner;

import database_management.Database;

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
                    break;
                case "4":
                    scan.close();
                    return;
                default:
                    break;
            }

        }
    }

    /**
     * Customer menu entry point
     */
    public void customer_start() {
        Scanner scan = new Scanner(System.in);
        CustomerMenu menu = new CustomerMenu();

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
                    scan.close();
                    return;
                default:
                    break;
            }

        }
    }
}
