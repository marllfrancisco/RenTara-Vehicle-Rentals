/**
 * RenTara: Vehicle-Renting-Services
 * 
 * A simple vehicle renting service for our OOP Final Terms Project
 *  utilizes SQLITE3 and CLI functionality via Scanner
 * 
 * Licensed under Apache-2.0 license
 */
package com.myapp;

import java.util.Optional;

import database_management.Database;
import menus.*;
import models.User;

public class Main {
    public static void main(String[] args) {

        // Object Initialization 
        Database db = new Database("RenTara.db");
        Login login = new Login(db);
        MainMenu main_menu = new MainMenu(db);

        // MAINEST Loop
        while (true) {
            // Start login/register
            Optional<User> result = login.start();
            
            if (result.isEmpty()) { 
                System.out.println("System Exiting.");
                break; 
            }
            
            // Logged-in user
            User user = result.get();

            // route to correct menu
            if (user.isAdmin()) { 
                main_menu.admin_start(); 
            } else { 
                main_menu.customer_start(user); 
            } 
            
            // the code drops down here, when logged out EITHER admin or cust
            // loop restarts, then to login.start() again
        }

        System.out.print("\033[H\033[2J");
    }
}