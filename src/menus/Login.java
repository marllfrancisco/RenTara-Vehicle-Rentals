
package menus;

import database_management.Database;
import models.User;
import models.UserRepository;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

public class Login {

    Database db;
    Scanner scan = new Scanner(System.in);

    public Login(Database db){
        this.db = db;
    }

    private String ask_info(String question) {
        System.out.print("Enter " + question + ": ");
        return scan.nextLine();
    }

    /**
     * Account Registration Function
     */
    private void register() {
    	// Instantiate repository outside the loop to save resources
        UserRepository urepo = new UserRepository(db.getConnection());
        
        while(true) {
            System.out.print("\033[H\033[2J"); //Clear screen

            try {

                System.out.print("\nCreate Username: ");
                String username = scan.nextLine();
                if (username.isEmpty()) return;
                	
                
                // Username taken check:
                if (urepo.findByUsername(username).isPresent()) {
                    System.out.println("\n[Error] Username '" + username + "' is already taken.");
                    System.out.println("Press Enter to try again...");
                    scan.nextLine(); // Pause
                    continue; // Restarts the loop to ask for username again
                }
                
                System.out.print("Create Password: ");
                String password = scan.nextLine();
                if (password.isEmpty()) return;
                
                System.out.print("Full name: ");
                String fullName = scan.nextLine();
                if (fullName.isEmpty()) return;

                // Create new User with corresponding information
                User new_user = new User(username, password, "Customer", fullName);

                // Insert to repository
                urepo.insert(new_user);
                System.out.println("\n[Success] Account created!");
                return;
                
            } catch (Exception e) { e.printStackTrace(); }
        }

    }

    /**
     * Login actual function
     * <p>
     * Gets user input with 3 allowed incorrect attempts
     * 
     * @return User | null
     */
    private Optional<User> login(){
        UserRepository urep = new UserRepository(db.getConnection());
        int attempts = 3;

        while (attempts > 0) {
            System.out.print("\033[H\033[2J");

            String username = ask_info("Username");
            if (username.isEmpty()) continue;

            try {
                // check Username
                Optional<User> existingUser = urep.findByUsername(username);
                
                if (existingUser.isEmpty()) {
                    attempts -= 1;
                    System.out.println("\n[Error] Username '" + username + "' does not exist.");
                    System.out.println("Attempts remaining: " + attempts);
                   
                    if(attempts > 0) {
                    	System.out.println("Press Enter to continue...");
                    	scan.nextLine();
                    } else {
                    	System.out.println("System Exiting.");
                    }
                    
                    continue; 
                }

                String password = ask_info("Password");
                if (password.isEmpty()) continue;

                //checks Username + Password 
                Optional<User> user = urep.findByUserAndPass(username, password);
                if (user.isEmpty()) {
                    attempts -= 1;
                    System.out.println("\n[Error] Incorrect password.");
                    System.out.println("Attempts remaining: " + attempts);
                    
                    if(attempts > 0) {
                    	System.out.println("Press Enter to continue...");
                    	scan.nextLine();
                    } else {
                    	System.out.println("System Exiting.");
                    }
                    continue;
                }
                
                User custUser = existingUser.get();
                System.out.println("\n[Success] Logging In...");
                System.out.println("Welcome, "+ custUser.getFullName());
                return user;

            } catch (SQLException e) {
                // catches errors for BOTH database queries above
                e.printStackTrace();
                attempts -= 1;
                System.out.println("[Error] in Database. \nPress Enter to continue...");
                scan.nextLine();
            }
        }   
        // dont scanner.close here
        // it breaks loop when failed 3 times
        return Optional.empty();
    }
    

    public Optional<User> start() {
        while (true) { // keeps menu alive
            // menu always starts fresh
            System.out.print("\033[H\033[2J"); 
            System.out.flush(); // Ensure it clears immediately

            // Print the menu
            System.out.print(MenuStrings.LOGIN_STRING);
            String selected = scan.nextLine().trim(); // avoid accidental space errors

            // Handle the logic
            switch (selected) {
                case "1":
                    return login(); // Only exits the loop if login returns a user
                case "2":
                    register();
                    break; // this loops back to the menu
                case "3":
                	System.out.println("\nThank you for considering RenTara.");
                    scan.close();
                    return Optional.empty();
                default:
                    // Handle invalid input
                    System.out.println("\n[Error] Invalid option: '" + selected+ "'");
                    System.out.println("\nPress Enter to continue...");
                    scan.nextLine(); // Pause so they can read the error
                    break;
            }
        }
    }
}