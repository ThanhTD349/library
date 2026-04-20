package com.library.main;

import com.library.model.*;
import com.library.service.*;
import com.library.util.InputValidator;
import java.util.List;

public class Main {
    private static final CategoryService categoryService = new CategoryService();
    private static final BookService bookService = new BookService();
    private static final UserService userService = new UserService();
    private static final AuthService authService = new AuthService(userService);
    private static final BorrowService borrowService = new BorrowService(bookService);

    public static void main(String[] args) {
        // Test database connection
        com.library.util.DatabaseContext.testConnection();
        while (true) {
            System.out.println("\n========== LIBRARY MANAGEMENT SYSTEM ==========");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
            int choice = InputValidator.getInt("Choose option: ");

            switch (choice) {
                case 1: login(); break;
                case 2: register(); break;
                case 0: System.exit(0);
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void login() {
        String code = InputValidator.getString("Student Code: ");
        String pass = InputValidator.getString("Password: ");
        if (authService.login(code, pass)) {
            System.out.println("Login successful! Welcome " + authService.getCurrentUser().getName());
            if (authService.isAdmin()) adminMenu();
            else userMenu();
        } else {
            System.out.println("Login failed. Check your credentials or account status.");
        }
    }

    private static void register() {
        String name = InputValidator.getNonEmptyString("Full Name: ");
        String code = InputValidator.getNonEmptyString("Student Code: ");
        String pass = InputValidator.getNonEmptyString("Password: ");
        authService.register(name, code, pass);
        System.out.println("Registration successful.");
    }

    // --- ADMIN MENUS ---
    private static void adminMenu() {
        while (authService.isAdmin()) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. Manage Books");
            System.out.println("2. Manage Categories");
            System.out.println("3. Manage Users");
            System.out.println("4. Manage Borrow Records");
            System.out.println("5. Statistics");
            System.out.println("0. Logout");
            int choice = InputValidator.getInt("Choose option: ");

            switch (choice) {
                case 1: manageBooks(); break;
                case 2: manageCategories(); break;
                case 3: manageUsers(); break;
                case 4: manageAllRecords(); break;
                case 5: showStatistics(); break;
                case 0: authService.logout(); break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void manageBooks() {
        System.out.println("\n--- BOOK MANAGEMENT ---");
        System.out.println("1. Add Book");
        System.out.println("2. Edit Book");
        System.out.println("3. Delete Book");
        System.out.println("4. List All Books");
        System.out.println("0. Back");
        int choice = InputValidator.getInt("Choose option: ");

        switch (choice) {
            case 1:
                String name = InputValidator.getNonEmptyString("Name: ");
                String author = InputValidator.getNonEmptyString("Author: ");
                listAllCategories();
                String catId = InputValidator.getString("Category ID: ");
                int qty = InputValidator.getInt("Quantity: ");
                bookService.addBook(name, author, catId, qty);
                break;
            case 2:
                String id = InputValidator.getString("Book ID to edit: ");
                String nName = InputValidator.getString("New Name: ");
                String nAuthor = InputValidator.getString("New Author: ");
                String nCat = InputValidator.getString("New Category ID: ");
                int nQty = InputValidator.getInt("New Quantity: ");
                boolean result = bookService.updateBook(id, nName, nAuthor, nCat, nQty);
                break;
            case 3:
                String dId = InputValidator.getString("Book ID to delete: ");
                bookService.deleteBook(dId);
                break;
            case 4:
                bookService.getAllBooks().forEach(System.out::println);
                break;
        }
    }

    private static void manageCategories() {
        System.out.println("\n--- CATEGORY MANAGEMENT ---");
        System.out.println("1. Add Category");
        System.out.println("2. Edit Category");
        System.out.println("3. Delete Category");
        System.out.println("4. List All Categories");
        int choice = InputValidator.getInt("Choose option: ");
        switch (choice) {
            case 1:
                String name = InputValidator.getNonEmptyString("Category Name: ");
                categoryService.addCategory(name);
                break;
            case 2:
                String id = InputValidator.getString("Category ID: ");
                String nName = InputValidator.getString("New Name: ");
                categoryService.updateCategory(id, nName);
                break;
            case 3:
                String dId = InputValidator.getString("Category ID: ");
                categoryService.deleteCategory(dId);
                break;
            case 4:
                listAllCategories();
                break;
        }
    }

    private static void listAllCategories() {
        categoryService.getAllCategories().forEach(System.out::println);
    }

    private static void manageUsers() {
        System.out.println("\n--- USER MANAGEMENT ---");
        System.out.println("1. List Users");
        System.out.println("2. Search User");
        System.out.println("3. Lock/Unlock User");
        System.out.println("4. Change User Role");
        int choice = InputValidator.getInt("Choose option: ");
        switch (choice) {
            case 1: userService.getAllUsers().forEach(System.out::println); break;
            case 2: 
                String kw = InputValidator.getString("Keyword: ");
                userService.searchUsers(kw).forEach(System.out::println);
                break;
            case 3:
                String id = InputValidator.getString("User ID: ");
                String status = InputValidator.getString("New Status (ACTIVE/LOCKED): ");
                if (userService.updateStatus(id, status)) System.out.println("Status updated.");
                else System.out.println("Update failed.");
                break;
            case 4:
                String rId = InputValidator.getString("User ID: ");
                String role = InputValidator.getString("New Role (ADMIN/USER): ");
                if (userService.updateRole(rId, role)) System.out.println("Role updated successfully.");
                else System.out.println("Update failed.");
                break;
        }
    }

    private static void manageAllRecords() {
        borrowService.getAllRecords().forEach(System.out::println);
    }

    private static void showStatistics() {
        System.out.println("\n--- STATISTICS ---");
        System.out.println("Total Books: " + bookService.getAllBooks().size());
        System.out.println("Borrowing Records: " + borrowService.getAllRecords().stream().filter(r -> "BORROWING".equals(r.getStatus())).count());
        System.out.println("Total Users: " + userService.getAllUsers().size());
    }

    // --- USER MENUS ---
    private static void userMenu() {
        while (authService.getCurrentUser() != null && !authService.isAdmin()) {
            System.out.println("\n--- USER MENU ---");
            System.out.println("1. Search Books");
            System.out.println("2. Borrow Book");
            System.out.println("3. Return Book");
            System.out.println("4. Extend Book");
            System.out.println("5. My Borrow Records");
            System.out.println("0. Logout");
            int choice = InputValidator.getInt("Choose option: ");

            switch (choice) {
                case 1:
                    String kw = InputValidator.getString("Keyword to search: ");
                    bookService.searchBooks(kw).forEach(System.out::println);
                    break;
                case 2:
                    String query = InputValidator.getString("Enter book name or ID to search: ");
                    List<Book> matches = bookService.searchBooks(query);
                    if (matches.isEmpty()) {
                        System.out.println("No books found matching '" + query + "'");
                    } else if (matches.size() == 1) {
                        Book b = matches.get(0);
                        System.out.println("Found: " + b);
                        String confirm = InputValidator.getString("Borrow this book? (y/n): ");
                        if (confirm.equalsIgnoreCase("y")) {
                            System.out.println(borrowService.borrowBook(authService.getCurrentUser().getId(), b.getId()));
                        }
                    } else {
                        System.out.println("Multiple books found:");
                        matches.forEach(System.out::println);
                        String bId = InputValidator.getString("Enter the exact Book ID to borrow (or press Enter to cancel): ");
                        if (!bId.isEmpty()) {
                            System.out.println(borrowService.borrowBook(authService.getCurrentUser().getId(), bId));
                        }
                    }
                    break;
                case 3:
                    String rId = InputValidator.getString("Record ID to return: ");
                    System.out.println(borrowService.returnBook(rId));
                    break;
                case 4:
                    String reId = InputValidator.getString("Record ID to extend: ");
                    int days = InputValidator.getInt("Days: ");
                    System.out.println(borrowService.extendBook(reId, days));
                    break;
                case 5:
                    borrowService.getUserRecords(authService.getCurrentUser().getId()).forEach(System.out::println);
                    break;
                case 0: authService.logout(); break;
                default: System.out.println("Invalid option.");
            }
        }
    }
}
