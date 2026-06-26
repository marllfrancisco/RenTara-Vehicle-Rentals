package com.myapp;

import database_management.Database;
import menus.*;

public class Main {
    public static void main(String[] args) {
        Database db = new Database("RenTara.db");

        Login login = new Login(db);
        MainMenu main_menu = new MainMenu(db);
        if (login.start() == true) {
            main_menu.start();
        }

        System.out.print("\033[H\033[2J");
    }
}