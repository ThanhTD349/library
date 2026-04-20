package com.library.service;

import com.library.model.Category;
import com.library.util.DatabaseContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CategoryService {

    public CategoryService() {
        // No need to load from file anymore
    }

    public void addCategory(String name) {
            String id = "C" + String.format("%02d", getAllCategories().size() + 1);        String sql = "INSERT INTO categories (id, name) VALUES (?, ?)";
            try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateCategory(String id, String newName) {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCategory(String id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        try (Connection conn = DatabaseContext.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Category(rs.getString("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Category getCategoryById(String id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Category(rs.getString("id"), rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
