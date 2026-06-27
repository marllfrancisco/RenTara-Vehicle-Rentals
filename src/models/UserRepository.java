package models;

import java.sql.*;
import java.util.Optional;
import java.util.function.Function;

public class UserRepository extends BaseRepository<User, Integer> {

    public UserRepository(Connection conn) {
        super(conn);
    }

    @Override
    protected String getTableName() { return "users"; }

    @Override
    protected String getIdColumn() { return "user_id"; }

    @Override
    protected String getInsertColumns() {
        return "username, password, role, full_name, is_kyc_approved";
    }

    @Override
    protected String getInsertPlaceholders() {
        return "?, ?, ?, ?, ?";
    }

    @Override
    protected String getUpdateColumns() {
        return "username = ?, password = ?, role = ?, full_name = ?, is_kyc_approved = ?";
    }

    @Override
    protected int getUpdateParamCount() { return 5; }

    @Override
    protected void setInsertParams(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getRole());
        ps.setString(4, user.getFullName());
        ps.setInt(5, user.isKycApproved() ? 1 : 0);
    }

    @Override
    protected void setUpdateParams(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getRole());
        ps.setString(4, user.getFullName());
        ps.setInt(5, user.isKycApproved() ? 1 : 0);
    }

    @Override
    protected Function<ResultSet, User> getMapper() {
        return rs -> {
            User user = new User();
            try {
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setFullName(rs.getString("full_name"));
                user.setKycApproved(rs.getInt("is_kyc_approved") == 1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return user;
        };
    }

    // Custom queries specific to User
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(getMapper().apply(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<User> findByUserAndPass(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(getMapper().apply(rs));
                }
            }
        }
        return Optional.empty();
    }
}
