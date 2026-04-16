package com.library.model;

import java.io.Serializable;
import java.time.LocalDate;

public class BorrowRecord implements Serializable {
    private String id;
    private String userId;
    private String bookId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private String status; // "BORROWING", "RETURNED", "OVERDUE"

    public BorrowRecord(String id, String userId, String bookId, LocalDate borrowDate, LocalDate returnDate, String status) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("RecordID: %s | UserID: %s | BookID: %s | Borrow: %s | Return: %s | Status: %s", 
                id, userId, bookId, borrowDate, returnDate, status);
    }
}
