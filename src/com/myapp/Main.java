package com.myapp;

import database_management.Database;
import menus.*;

public class Main {
    public static void main(String[] args) {
        Database db = new Database("RenTara.db");

        Login login = new Login(db);
        MainMenu main_menu = new MainMenu(db);

        String value = login.start();
        if (value == "Admin") {
            main_menu.admin_start();
        }
        else if (value == "Customer") {
            main_menu.customer_start();
        }

        System.out.print("\033[H\033[2J");
    }
}