package com.library.service;

import com.library.model.Book;
import com.library.util.DatabaseContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookService {

    public BookService() {
        // No need to load from file
    }
    private boolean bookExists(String id) {
        String sql = "SELECT 1 FROM books WHERE id = ?";

        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            return false;
        }
    }
    private boolean categoryExists(String id) {
        String sql = "SELECT 1 FROM categories WHERE id = ?";

        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            return false;
        }
    }
    public void addBook(String name, String author, String categoryId, int quantity) {
        String id = "B" + String.format("%03d", getAllBooks().size() + 1);
        String sql = "INSERT INTO books (id, name, author, category_id, quantity) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, author);
            pstmt.setString(4, categoryId);
            pstmt.setInt(5, quantity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateBook(String id, String name, String author, String categoryId, int quantity) {
        if (quantity < 0) {
            System.out.println("Quantity must be >= 0");
            return false;
        }

        if (!bookExists(id)) {
            System.out.println("Book not found!");
            return false;
        }

        if (!categoryExists(categoryId)) {
            System.out.println("Category does not exist!");
            return false;
        }
        String sql = "UPDATE books SET name = ?, author = ?, category_id = ?, quantity = ? WHERE id = ?";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, author);
            pstmt.setString(3, categoryId);
            pstmt.setInt(4, quantity);
            pstmt.setString(5, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Book updated successfully");
                return true;
            } else {
                System.out.println("Update failed!");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBook(String id) {
        if (id == null || id.trim().isEmpty()) {
            System.out.println("Invalid ID!");
            return false;
        }

        if (!bookExists(id)) {
            System.out.println("Book not found!");
            return false;
        }
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Book deleted successfully");
                return true;
            } else {
                System.out.println("Delete failed!");
                return false;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Cannot delete book (it is being borrowed)");
            return false;


        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = DatabaseContext.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Book(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("author"),
                        rs.getString("category_id"),
                        rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE name LIKE ? OR author LIKE ? OR id LIKE ?";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String query = "%" + keyword + "%";
            pstmt.setString(1, query);
            pstmt.setString(2, query);
            pstmt.setString(3, query);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Book(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("author"),
                            rs.getString("category_id"),
                            rs.getInt("quantity")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Book getBookById(String id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Book(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("author"),
                            rs.getString("category_id"),
                            rs.getInt("quantity")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateQuantity(String id, int delta) {
        String sql = "UPDATE books SET quantity = quantity + ? WHERE id = ?";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, delta);
            pstmt.setString(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
