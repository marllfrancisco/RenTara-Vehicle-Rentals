package models;

//Base class
public class Vehicle implements Model<Integer> {
    private int vehicleId;
    private int ownerId; // Shared field
    private String vehicleType;
    private String brandModel;
    private double dailyRate;
    private boolean isAvailable; // Shared field

    public Vehicle () {}

    // Base constructor to handle common fields
    public Vehicle(int ownerId, String vehicleType, String brandModel, double dailyRate) {
        this.ownerId = ownerId;
        this.vehicleType = vehicleType;
        this.brandModel = brandModel;
        this.dailyRate = dailyRate;
        this.isAvailable = true;
    }
    
    @Override public Integer getId() { return vehicleId; }
    @Override public void setId(Integer vehicleId) { this.vehicleId = vehicleId; }
    
    // Standard Getters/Setters for shared fields

    public int getVehicleId() { return vehicleId; }
    public int getOwnerId() { return ownerId; }
    public String getVehicleType() { return vehicleType; }
    public String getBrandModel() { return brandModel; }
    public double getDailyRate() { return dailyRate; }
    public boolean isAvailable() { return isAvailable; }

    public void setVehicleId(Integer vehicleId) { this.vehicleId = vehicleId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public void setBrandModel(String brandModel) { this.brandModel = brandModel; }
    public void setDailyRate(Double dailyRate) { this.dailyRate = dailyRate; }
    public void setIsAvailable(boolean isAvailable) { this.isAvailable = isAvailable; }
}
