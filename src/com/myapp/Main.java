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
        User user = new User();

        // Get user object using Login
        Optional<User> result = login.start();

        if (result.isPresent()) { user = result.get(); }
        else return;

        if (user.isAdmin()) { main_menu.admin_start(); }
        else { main_menu.customer_start(user); } 

        System.out.print("\033[H\033[2J");
    }
}