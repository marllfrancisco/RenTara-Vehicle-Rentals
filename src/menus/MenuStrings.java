package menus;

public class MenuStrings {
    private MenuStrings(){}
    
    /* I Give the Login and Main Menu a pleasing appearance
     * All menus are exactly 45 characters wide to keep the console looking uniform.
     * The '\s' at the very end ensures the space after "Entry:" is not deleted by Java.
     */
    
    public static final String LOGIN_STRING = """
            
            =============================================
                       RenTara Vehicle Services          
            =============================================
                           LOGIN / REGISTER               
            ---------------------------------------------
              1. Log in
              2. Register
              3. Exit
            ---------------------------------------------
            Entry:\s""";

    public static final String ADMIN_STRING = """
            
            =============================================
                           ADMIN MAIN MENU               
            =============================================
              1. Approve KYC
              2. View All Users
              3. View Revenue Report
              4. Logout
            ---------------------------------------------
            Entry:\s""";

    public static final String CUSTOMER_STRING = """
            
            =============================================
                          CUSTOMER MAIN MENU             
            =============================================
              1. Rent a Vehicle
              2. List my Vehicle for Rent
              3. Return a Rented Vehicle
              4. Unlist my Vehicle for Rent
              5. Edit my Vehicle
              6. Logout
            ---------------------------------------------
            Entry:\s""";
}
