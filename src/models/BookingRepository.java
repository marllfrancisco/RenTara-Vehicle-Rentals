package models;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BookingRepository extends BaseRepository<Booking, Integer> {

    public BookingRepository(Connection conn) {
        super(conn);
    }

    @Override
    protected String getTableName() { return "bookings"; }

    @Override
    protected String getIdColumn() { return "booking_id"; }

    @Override
    protected String getInsertColumns() {
        return "guest_id, vehicle_id, days_rented, total_fee, status";
    }

    @Override
    protected String getInsertPlaceholders() {
        return "?, ?, ?, ?, ?";
    }

    @Override
    protected String getUpdateColumns() {
        return "guest_id = ?, vehicle_id = ?, days_rented = ?, total_fee = ?, status = ?";
    }

    @Override
    protected int getUpdateParamCount() { return 5; }

    @Override
    protected void setInsertParams(PreparedStatement ps, Booking booking) throws SQLException {
        ps.setInt(1, booking.getUserId());
        ps.setInt(2, booking.getVehicleId());
        ps.setInt(3, booking.getDaysRented());
        ps.setDouble(4, booking.getTotalFee());
        ps.setString(5, booking.getStatus());
    }

    @Override
    protected void setUpdateParams(PreparedStatement ps, Booking booking) throws SQLException {
        ps.setInt(1, booking.getUserId());
        ps.setInt(2, booking.getVehicleId());
        ps.setInt(3, booking.getDaysRented());
        ps.setDouble(4, booking.getTotalFee());
        ps.setString(5, booking.getStatus());
    }

    @Override
    protected Function<ResultSet, Booking> getMapper() {
        return rs -> {
            Booking booking = new Booking();
            try {
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setUserId(rs.getInt("guest_id"));
                booking.setVehicleId(rs.getInt("vehicle_id"));
                booking.setDaysRented(rs.getInt("days_rented"));
                booking.setTotalFee(rs.getDouble("total_fee"));
                booking.setStatus(rs.getString("status"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return booking;
        };
    }

    // Custom queries specific to Vehicle
    public List<Booking> findAllRentsOfUser(Integer ownerId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
            SELECT 
                b.booking_id, 
                b.guest_id,
                b.vehicle_id,
                b.days_rented,
                b.total_fee, 
                b.status,
                v.brand_model 
            FROM bookings b 
            JOIN vehicles v ON b.vehicle_id = v.vehicle_id 
            WHERE b.guest_id = ? AND b.status = 'Pending'
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    Booking book = getMapper().apply(rs);
                    book.setVehicleModel(rs.getString("brand_model"));
                    bookings.add(book);
                }
            }
        }
        return bookings;
    }

    // TOTAL REVENUE --------------
    public double getTotalRevenue() throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(total_fee), 0) AS total_revenue
            FROM bookings
            WHERE status = 'Paid'
            """;
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("total_revenue");
            }
        }
        return 0.0;
    }
    
    public void printTotal() throws SQLException {
        double total = getTotalRevenue();
        
        System.out.printf("Completed Bookings Revenue: PHP %,.2f%n", total);
    }
    
    // PENDING REVENUE --------------
    public double getPendingRevenue() throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(total_fee), 0) AS pending_revenue
            FROM bookings
            WHERE status = 'Pending'
            """;
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("pending_revenue");
            }
        }
        return 0.0;
    }
    
    public void printPending() throws SQLException {
        double total = getPendingRevenue();
        
        System.out.printf("Pending Bookings Revenue: PHP %,.2f%n", total);
    }

    public List<Map<String, Object>> findMostRented() throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = """
            SELECT 
                v.vehicle_id,
                v.vehicle_type,
                v.brand_model,
                v.daily_rate,
                COUNT(b.booking_id) AS rental_count,
                COALESCE(SUM(b.days_rented), 0) AS total_days,
                COALESCE(SUM(b.total_fee), 0) AS total_earned
            FROM vehicles v
            LEFT JOIN bookings b ON v.vehicle_id = b.vehicle_id
            GROUP BY v.vehicle_id
            ORDER BY rental_count DESC, total_earned DESC
            """;
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("vehicle_type", rs.getString("vehicle_type"));
                row.put("brand_model", rs.getString("brand_model"));
                row.put("daily_rate", rs.getDouble("daily_rate"));
                row.put("rental_count", rs.getInt("rental_count"));
                row.put("total_days", rs.getInt("total_days"));
                row.put("total_earned", rs.getDouble("total_earned"));
                results.add(row);
            }
        }
        return results;
    }

    public void print() throws SQLException {
        List<Map<String, Object>> data = findMostRented();
        
        System.out.println("MOST RENTED VEHICLES");
        System.out.println("=".repeat(80));
        System.out.printf("%-15s %-12s %10s %10s %5s %15s%n",
            "Type", "Model", "Per Day", "Rentals", "Days", "Total Earned");
        System.out.println("=".repeat(80));
        
        if (data.isEmpty()) {
            System.out.println("No data available");
        } else {
        for (Map<String, Object> row : data) {
                System.out.printf("%-15s %-12s %10.2f %10d %5d %15.2f%n",
                    row.get("vehicle_type"),
                    row.get("brand_model"),
                    row.get("daily_rate"),
                    row.get("rental_count"),
                    row.get("total_days"),
                    row.get("total_earned"));
            }
        }
        System.out.println("=".repeat(80));
    }
}
