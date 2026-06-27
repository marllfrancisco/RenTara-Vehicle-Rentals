package menus;

import java.util.Scanner;

import database_management.Database;

public class MainMenu {
    Database db;

    public MainMenu(Database db){
        this.db = db;
    }

    public void admin_start(){
        Scanner scan = new Scanner(System.in);

        while(true) {
            System.out.print("\033[H\033[2J");

            switch (scan.nextLine()) {
                case "1":
                    
                    break;
                case "5":
                    scan.close();
                    return;
                default:
                    break;
            }

        }
    }

    public void customer_start() {

    }
}
