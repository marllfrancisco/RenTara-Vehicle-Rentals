package menus;

import database_management.Database;

public class Login {

    Database db;

    public Login(Database db){
        this.db = db;
    }

    public boolean start(){

        // Implement 3 attempt registry using while loop
        // Use db connection to compare input and available records via SELECT FROM WHERE
        System.out.println("Test");

        return true;
    }
}