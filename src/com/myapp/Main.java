package com.myapp;

import java.util.Optional;

import database_management.Database;
import menus.*;
import models.User;

public class Main {
    public static void main(String[] args) {
        Database db = new Database("RenTara.db");

        Login login = new Login(db);
        MainMenu main_menu = new MainMenu(db);
        Optional<User> result = login.start();
        User user;

        if (result.isPresent()) { user = result.get(); }
        else return;

        if (user.getRole().equals("Admin")) {
            main_menu.admin_start();
        }
        else if (user.getRole().equals("Customer")) {
            main_menu.customer_start();
        } 


        System.out.print("\033[H\033[2J");
    }
}