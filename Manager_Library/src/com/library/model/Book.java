package com.library.model;

import java.io.Serializable;

public class Book implements Serializable {
    private String id;
    private String name;
    private String author;
    private String categoryId;
    private int quantity;

    public Book(String id, String name, String author, String categoryId, int quantity) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.categoryId = categoryId;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Author: %s | CategoryID: %s | Qty: %d", 
                id, name, author, categoryId, quantity);
    }
}
