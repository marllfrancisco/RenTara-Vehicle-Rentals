package com.myapp;

import java.util.*;

import database_management.Database;
import menus.*;
import models.User;

public class Main {
	static Scanner scan = new Scanner(System.in);
	
    public static void main(String[] args) {
        Database db = new Database("RenTara.db");
        
        Login login = new Login(db);
        MainMenu main_menu = new MainMenu(db);
        User user = new User();

        Optional<User> result = login.start();
        if (result.isPresent()) { user = result.get(); }
        else return;

        if (user.isAdmin()) { main_menu.admin_start(); }
        else { main_menu.customer_start(user); } 

        System.out.print("\033[H\033[2J");
    }
}