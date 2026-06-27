package models;

//Base class
public abstract class Vehicle {
    protected int vehicleId;
    protected int ownerId; // Shared field
    protected String brandModel;
    protected double dailyRate;
    protected boolean isAvailable; // Shared field

    // Base constructor to handle common fields
    public Vehicle(int vehicleId, int ownerId, String brandModel, double dailyRate, boolean isAvailable) {
        this.vehicleId = vehicleId;
        this.ownerId = ownerId;
        this.brandModel = brandModel;
        this.dailyRate = dailyRate;
        this.isAvailable = isAvailable;
    }

    public abstract void displayDetails();
    
    // Standard Getters/Setters for shared fields
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public int getOwnerId() { return ownerId; }
}


class VehicleFactory {
    public static Vehicle createVehicle(String type, int id, int ownerId, String brand, double rate, boolean available) {
        return switch (type.toLowerCase()) {
            case "suv" -> new SUV(id, ownerId, brand, rate, available);
            case "motorcycle" -> new Motorcycle(id, ownerId, brand, rate, available);
            // fill this with other vehicles pa
            default -> new DefaultVehicle(id, ownerId, brand, rate, available);
        };
    }
}

// ------------------------------------------------------------------------
// ALL CHILD CLASSES OF VEHICLE 
class DefaultVehicle extends Vehicle {
    // We pass the common values up to the 'super' constructor
    public DefaultVehicle(int id, int ownerId, String brandModel, double rate, boolean isAvailable) {
        super(id, ownerId, brandModel, rate, isAvailable);
    }
    @Override
    public void displayDetails() {
        // We can now access 'brandModel' and 'isAvailable' directly
        System.out.println("SUV [" + brandModel + "] - Available: " + isAvailable);
    }
}


class Motorcycle extends Vehicle {   
    public Motorcycle(int id, int ownerId, String brandModel, double rate, boolean isAvailable) {
        super(id, ownerId, brandModel, rate, isAvailable);
    }
    @Override
    public void displayDetails() {
        System.out.println("SUV [" + brandModel + "] - Available: " + isAvailable);
    }
}


class SUV extends Vehicle {
    public SUV(int id, int ownerId, String brandModel, double rate, boolean isAvailable) {
        super(id, ownerId, brandModel, rate, isAvailable);
    }
    @Override
    public void displayDetails() {
        System.out.println("SUV [" + brandModel + "] - Available: " + isAvailable);
    }
}
