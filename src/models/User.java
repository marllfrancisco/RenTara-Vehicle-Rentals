package models;

import java.time.LocalDateTime;

public class User implements Model<Integer> {
    private int userId;
    private String username;
    private String password;
    private String role;
    private String fullName;
    private boolean isKycApproved;
    private LocalDateTime createdAt;

    public User() {}

    public User(String username, String password, String role, String fullName) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.isKycApproved = false;
    }

    @Override public Integer getId() { return userId; }
    @Override public void setId(Integer userId) { this.userId = userId; }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getFullName() { return fullName; }
    public boolean isKycApproved() { return isKycApproved; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setUserId(int userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setKycApproved(boolean isKycApproved) { this.isKycApproved = isKycApproved; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', role='%s', kyc=%s}",
            userId, username, role, isKycApproved);
    }
}
