package com.myapp;

import java.util.Scanner;

import database_management.Database;
import menus.*;

public class Main {
    public static void main(String[] args) {
        Database db = new Database("RenTara.db");

        Login login = new Login(db);
        MainMenu main_menu = new MainMenu(db);

        String role = login.start();
        if (role.equals("admin")) {
            main_menu.admin_start();
        }
        else if (role.equals("customer")) {
            main_menu.customer_start();
        }
        System.out.print(role);

        System.out.print("\033[H\033[2J");
    }
}