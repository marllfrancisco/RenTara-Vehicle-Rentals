package models;

public class Booking implements Model<Integer> {
    private int bookingId;
    private int userId;    // Foreign Key to User
    private int vehicleId; // Foreign Key to Vehicle
    private int daysRented;
    private double totalFee;
    private String status; // "Pending", "Approved", "Completed"

    private String vehicleModel;

    public Booking() {}

    public Booking(int userId, int vehicleId, int daysRented, double totalFee) {
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.daysRented = daysRented;
        this.totalFee = totalFee;
        this.status = "Pending";
    }

    @Override public Integer getId() { return bookingId; }
    @Override public void setId(Integer bookingId) { this.bookingId = bookingId; }

    // Getters & Setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public int getDaysRented() { return daysRented; }
    public void setDaysRented(int daysRented) { this.daysRented = daysRented; }
    public double getTotalFee() { return totalFee; }
    public void setTotalFee(double totalFee) { this.totalFee = totalFee; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }
}