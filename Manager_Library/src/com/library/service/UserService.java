package com.library.service;

import com.library.model.User;
import com.library.util.DatabaseContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService {

    public UserService() {
        // Ensure admin exists
        if (getUserByStudentCode("admin") == null) {
            addUser("Admin", "admin", "admin123", "ADMIN");
        }
    }
    private String generateUserId() {
        String sql = "SELECT id FROM users ORDER BY id DESC LIMIT 1";

        try (Connection conn = DatabaseContext.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastId = rs.getString("id"); // ví dụ U003
                int num = Integer.parseInt(lastId.substring(1));
                return "U" + String.format("%03d", num + 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "U001";
    }
    public void addUser(String name, String studentCode, String password, String role) {
        String id = generateUserId();
        String sql = "INSERT INTO users (id, name, student_code, password, role, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, studentCode);
            pstmt.setString(4, password);
            pstmt.setString(5, role);
            pstmt.setString(6, "ACTIVE");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseContext.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new User(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("student_code"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<User> searchUsers(String keyword) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE name LIKE ? OR student_code LIKE ?";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String query = "%" + keyword + "%";
            pstmt.setString(1, query);
            pstmt.setString(2, query);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new User(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("student_code"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateStatus(String id, String status) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean updateRole(String id, String role) {
        String fixStatusSql = "UPDATE users SET status='ACTIVE' WHERE status='active'";
        String updateRoleSql = "UPDATE users SET role=? WHERE id=?";
        try (Connection conn = DatabaseContext.getConnection()) {
            try (PreparedStatement ps1 = conn.prepareStatement(fixStatusSql)) {
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement(updateRoleSql)) {
                ps2.setString(1, role.toUpperCase());
                ps2.setString(2, id);
                return ps2.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public User getUserByStudentCode(String studentCode) {
        String sql = "SELECT * FROM users WHERE student_code = ?";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("student_code"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserById(String id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("student_code"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
