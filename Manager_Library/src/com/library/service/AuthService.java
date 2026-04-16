package com.library.service;

import com.library.model.User;

public class AuthService {
    private UserService userService;
    private User currentUser;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public boolean login(String studentCode, String password) {
        User user = userService.getUserByStudentCode(studentCode);
        if (user != null && user.getPassword().equals(password)) {
            if ("LOCKED".equals(user.getStatus())) {
                System.out.println("Account is locked.");
                return false;
            }
            this.currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    public void register(String name, String studentCode, String password) {
        userService.addUser(name, studentCode, password, "USER");
    }
}
