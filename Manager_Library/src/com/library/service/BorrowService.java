package com.library.service;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.util.DatabaseContext;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BorrowService {
    private BookService bookService;

    public BorrowService(BookService bookService) {
        this.bookService = bookService;
    }

    public String borrowBook(String userId, String bookId) {
        Book book = bookService.getBookById(bookId);
        if (book == null) return "Book not found.";
        if (book.getQuantity() <= 0) return "Book is out of stock.";

        // Check if user already borrowing this book
        String checkSql = "SELECT COUNT(*) FROM borrow_records WHERE user_id = ? AND book_id = ? AND status = 'BORROWING'";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return "You are already borrowing this book.";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Internal error.";
        }

        String id = UUID.randomUUID().toString().substring(0, 8);
        LocalDate now = LocalDate.now();
        LocalDate returnDate = now.plusDays(14);

        String sql = "INSERT INTO borrow_records (id, user_id, book_id, borrow_date, return_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseContext.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, id);
                pstmt.setString(2, userId);
                pstmt.setString(3, bookId);
                pstmt.setDate(4, Date.valueOf(now));
                pstmt.setDate(5, Date.valueOf(returnDate));
                pstmt.setString(6, "BORROWING");
                pstmt.executeUpdate();

                bookService.updateQuantity(bookId, -1);
                conn.commit();
                return "Borrowed successfully. Return date: " + returnDate;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return "Transaction failed.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Connection error.";
        }
    }

    public String returnBook(String recordId) {
        String querySql = "SELECT * FROM borrow_records WHERE id = ?";
        try (Connection conn = DatabaseContext.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement qStmt = conn.prepareStatement(querySql)) {
                qStmt.setString(1, recordId);
                try (ResultSet rs = qStmt.executeQuery()) {
                    if (!rs.next()) return "Record not found.";
                    if (!"BORROWING".equals(rs.getString("status"))) return "Book already returned.";

                    String bookId = rs.getString("book_id");
                    String updateSql = "UPDATE borrow_records SET status = 'RETURNED', return_date = ? WHERE id = ?";
                    try (PreparedStatement uStmt = conn.prepareStatement(updateSql)) {
                        uStmt.setDate(1, Date.valueOf(LocalDate.now()));
                        uStmt.setString(2, recordId);
                        uStmt.executeUpdate();

                        bookService.updateQuantity(bookId, 1);
                        conn.commit();
                        return "Returned successfully.";
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return "Transaction failed.";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Connection error.";
        }
    }

    public String extendBook(String recordId, int days) {
        String querySql = "SELECT return_date, status FROM borrow_records WHERE id = ?";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(querySql)) {
            pstmt.setString(1, recordId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return "Record not found.";
                if (!"BORROWING".equals(rs.getString("status"))) return "Cannot extend returned book.";

                LocalDate currentReturnDate = rs.getDate("return_date").toLocalDate();
                LocalDate newReturnDate = currentReturnDate.plusDays(days);

                String updateSql = "UPDATE borrow_records SET return_date = ? WHERE id = ?";
                try (PreparedStatement uStmt = conn.prepareStatement(updateSql)) {
                    uStmt.setDate(1, Date.valueOf(newReturnDate));
                    uStmt.setString(2, recordId);
                    uStmt.executeUpdate();
                    return "Extended successfully. New return date: " + newReturnDate;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Failed to extend.";
        }
    }

    public List<BorrowRecord> getUserRecords(String userId) {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE user_id = ?";
        try (Connection conn = DatabaseContext.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new BorrowRecord(
                            rs.getString("id"),
                            rs.getString("user_id"),
                            rs.getString("book_id"),
                            rs.getDate("borrow_date").toLocalDate(),
                            rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<BorrowRecord> getAllRecords() {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records";
        try (Connection conn = DatabaseContext.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new BorrowRecord(
                        rs.getString("id"),
                        rs.getString("user_id"),
                        rs.getString("book_id"),
                        rs.getDate("borrow_date").toLocalDate(),
                        rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
