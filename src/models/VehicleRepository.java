package models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class VehicleRepository extends BaseRepository<Vehicle, Integer> {

    public VehicleRepository(Connection conn) {
        super(conn);
    }

    @Override
    protected String getTableName() { return "vehicles"; }

    @Override
    protected String getIdColumn() { return "vehicle_id"; }

    @Override
    protected String getInsertColumns() {
        return "owner_id, vehicle_type, brand_model, daily_rate, is_available";
    }

    @Override
    protected String getInsertPlaceholders() {
        return "?, ?, ?, ?, ?";
    }

    @Override
    protected String getUpdateColumns() {
        return "owner_id = ?, vehicle_type = ?, brand_model = ?, daily_rate = ?, is_available = ?";
    }

    @Override
    protected int getUpdateParamCount() { return 5; }

    @Override
    protected void setInsertParams(PreparedStatement ps, Vehicle vehicle) throws SQLException {
        ps.setInt(1, vehicle.getOwnerId());
        ps.setString(2, vehicle.getVehicleType());
        ps.setString(3, vehicle.getBrandModel());
        ps.setDouble(4, vehicle.getDailyRate());
        ps.setInt(5, vehicle.isAvailable() ? 1 : 0);
    }

    @Override
    protected void setUpdateParams(PreparedStatement ps, Vehicle vehicle) throws SQLException {
        ps.setInt(1, vehicle.getOwnerId());
        ps.setString(2, vehicle.getVehicleType());
        ps.setString(3, vehicle.getBrandModel());
        ps.setDouble(4, vehicle.getDailyRate());
        ps.setInt(5, vehicle.isAvailable() ? 1 : 0);
    }

    @Override
    protected Function<ResultSet, Vehicle> getMapper() {
        return rs -> {
            Vehicle vehicle = new Vehicle();
            try {
                vehicle.setVehicleId(rs.getInt("vehicle_id"));
                vehicle.setOwnerId(rs.getInt("owner_id"));
                vehicle.setVehicleType(rs.getString("vehicle_type"));
                vehicle.setBrandModel(rs.getString("brand_model"));
                vehicle.setDailyRate(rs.getDouble("daily_rate"));
                vehicle.setIsAvailable(rs.getInt("is_available")==1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return vehicle;
        };
    }

    // Custom queries specific to Vehicle
    public List<Vehicle> findAllAvailable() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE is_available = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    vehicles.add(getMapper().apply(rs));
                }

            }
        }
        return vehicles;
    }

    public List<Vehicle> findAllAvailableOfOwner(Integer ownerId) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE owner_id = ? AND is_available = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    vehicles.add(getMapper().apply(rs));
                }
            }
        }
        return vehicles;
    }

    
    public int delete(Integer ownerId, Integer vehicleId) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE owner_id = ? AND vehicle_id = ? "
        		+ "AND is_available = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            ps.setInt(2, vehicleId);
            return ps.executeUpdate();
        }
    }

    
    public Optional<Vehicle> getVehicleOfOwner(Integer vehicleId, Integer ownerId) throws SQLException {
        String sql = "SELECT * FROM vehicles WHERE vehicle_id = ? AND owner_id = ? AND is_available = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            ps.setInt(2, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(getMapper().apply(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Vehicle> searchForKey(Integer ownerId, String contains) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE vehicle_type LIKE '%' || ? || '%' OR brand_model LIKE '%' || ? || '%'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contains);
            ps.setString(2, contains);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(getMapper().apply(rs));
                }
            }
        }
        return vehicles;
    }
}
