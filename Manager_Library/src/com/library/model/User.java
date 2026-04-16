package com.library.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String name;
    private String studentCode;
    private String password;
    private String role; // "ADMIN" or "USER"
    private String status; // "ACTIVE" or "LOCKED"

    public User(String id, String name, String studentCode, String password, String role, String status) {
        this.id = id;
        this.name = name;
        this.studentCode = studentCode;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Code: %s | Role: %s | Status: %s", 
                id, name, studentCode, role, status);
    }
}
