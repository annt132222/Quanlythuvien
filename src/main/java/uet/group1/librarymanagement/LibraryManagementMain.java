package uet.group1.librarymanagement;


import uet.group1.librarymanagement.dao.BookDao;
import uet.group1.librarymanagement.dao.UserDao;
import uet.group1.librarymanagement.dao.BookDaoImpl;
import uet.group1.librarymanagement.dao.UserDaoImpl;
import uet.group1.librarymanagement.Entities.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class LibraryManagementMain {
    private final Scanner sc = new Scanner(System.in);
    private final BookDao bookDao = new BookDaoImpl();
    private final UserDao userDao = new UserDaoImpl();
    private Person currentUser = null;

    public static void main(String[] args) {
        new LibraryManagementMain().run();
    }

    private void run() {
        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else if (currentUser.getRole() == Person.Role.ADMIN) {
                showAdminMenu();
            } else {
                showBorrowerMenu();
            }
        }
    }

    // === Pre-login screen ===
    private void showAuthMenu() {
        System.out.println("\n=== Welcome to Library CLI ===");
        System.out.println("[1] Login");
        System.out.println("[2] Register");
        System.out.println("[0] Exit");
        System.out.print("Select action: ");
        String cmd = sc.nextLine().trim();
        switch (cmd) {
            case "1": login();    break;
            case "2": register(); break;
            case "0": System.exit(0);
            default: System.out.println("Invalid option.");
        }
    }

    private void login() {
        System.out.print("  User ID: ");
        String id = sc.nextLine().trim();
        System.out.print("  Password: ");
        String pwd = sc.nextLine().trim();
        Optional<Person> opt = userDao.findById(id);
        if (opt.isPresent() && opt.get().getPassword().equals(pwd)) {
            currentUser = opt.get();
            System.out.println("Login successful! Hello, " + currentUser.getName());
        } else {
            System.out.println("Login failed.");
        }
    }

    private void register() {
        System.out.print("  New User ID: ");
        String id = sc.nextLine().trim();
        System.out.print("  Name: ");
        String name = sc.nextLine().trim();
        System.out.print("  Password: ");
        String pwd = sc.nextLine().trim();
        System.out.print("  Role (BORROWER or ADMIN): ");
        String roleStr = sc.nextLine().trim().toUpperCase();
        Person.Role role;
        try {
            role = Person.Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid role."); return;
        }
        Person u = (role == Person.Role.ADMIN)
                ? new Admin(id, name, pwd)
                : new Borrower(id, name, pwd);
        if (userDao.insert(u)) {
            System.out.println("Registered successfully.");
        } else {
            System.out.println("Registration failed (ID might exist).");
        }
    }

    // === Admin menu ===
    private void showAdminMenu() {
        System.out.println("\n=== Admin Menu ===");
        System.out.println("[1] Add Book");
        System.out.println("[2] Update Book");
        System.out.println("[3] Delete Book");
        System.out.println("[4] Search by Title");
        System.out.println("[5] Search by Author");
        System.out.println("[6] Display All Books");
        System.out.println("[7] Add User");
        System.out.println("[8] Update User");
        System.out.println("[9] Delete User");
        System.out.println("[10] Display All Users");
        System.out.println("[0] Logout");
        System.out.print("Select action: ");
        String cmd = sc.nextLine().trim();
        switch (cmd) {
            case "1": addBook();        break;
            case "2": updateBook();     break;
            case "3": deleteBook();     break;
            case "4": searchByTitle();  break;
            case "5": searchByAuthor(); break;
            case "6": displayAllBooks();break;
            case "7": addUser();        break;
            case "8": updateUser();     break;
            case "9": deleteUser();     break;
            case "10": displayAllUsers();break;
            case "0": currentUser = null; break;
            default: System.out.println("Invalid option.");
        }
    }

    // === Borrower menu ===
    private void showBorrowerMenu() {
        System.out.println("\n=== Borrower Menu ===");
        System.out.println("[1] Search by Title");
        System.out.println("[2] Search by Author");
        System.out.println("[3] Display All Books");
        System.out.println("[4] View Profile");
        System.out.println("[0] Logout");
        System.out.print("Select action: ");
        String cmd = sc.nextLine().trim();
        switch (cmd) {
            case "1": searchByTitle();  break;
            case "2": searchByAuthor(); break;
            case "3": displayAllBooks();break;
            case "4": viewProfile();     break;
            case "0": currentUser = null; break;
            default: System.out.println("Invalid option.");
        }
    }

    // === Book operations ===
    private void addBook() {
        try {
            System.out.print("  Author: ");
            String author = sc.nextLine().trim();
            System.out.print("  Title: ");
            String title = sc.nextLine().trim();
            System.out.print("  Format: ");
            String bookFormat = sc.nextLine().trim();
            System.out.print("  Description: ");
            String desc = sc.nextLine().trim();
            System.out.print("  Genre: ");
            String genre = sc.nextLine().trim();
            System.out.print("  Image URL: ");
            String img = sc.nextLine().trim();
            System.out.print("  ISBN: ");
            String isbn = sc.nextLine().trim();
            System.out.print("  ISBN13: ");
            String isbn13 = sc.nextLine().trim();
            System.out.print("  Link: ");
            String link = sc.nextLine().trim();
            System.out.print("  Pages: ");
            int pages = Integer.parseInt(sc.nextLine().trim());
            System.out.print("  Rating: ");
            BigDecimal rating = new BigDecimal(sc.nextLine().trim());
            System.out.print("  Reviews: ");
            int reviews = Integer.parseInt(sc.nextLine().trim());
            System.out.print("  Total Ratings: ");
            int totalRatings = Integer.parseInt(sc.nextLine().trim());

            Book b = new Book(0, author, bookFormat, desc,
                    genre, img, isbn, isbn13,
                    link, pages, rating, reviews,
                    title, totalRatings);
            if (bookDao.insert(b)) {
                System.out.println("Book added.");
            } else {
                System.out.println("Failed to add book.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    private void updateBook() {
        try {
            System.out.print("  Book ID: ");
            int id = Integer.parseInt(sc.nextLine().trim());
            Optional<Book> ob = bookDao.findById(id);
            if (!ob.isPresent()) {
                System.out.println("Book not found."); return;
            }
            // You can fetch ob.get() and allow partial edits; here we re-enter all fields:
            Book old = ob.get();
            System.out.print("  New Author ("+old.getAuthor()+"): ");
            String author = sc.nextLine().trim();
            System.out.print("  New Title ("+old.getTitle()+"): ");
            String title = sc.nextLine().trim();
            System.out.print("  New Format ("+old.getBookFormat()+"): ");
            String bookFormat = sc.nextLine().trim();
            System.out.print("  New Description: ");
            String desc = sc.nextLine().trim();
            System.out.print("  New Genre ("+old.getGenre()+"): ");
            String genre = sc.nextLine().trim();
            System.out.print("  New Image URL: ");
            String img = sc.nextLine().trim();
            System.out.print("  New ISBN ("+old.getIsbn()+"): ");
            String isbn = sc.nextLine().trim();
            System.out.print("  New ISBN13: ");
            String isbn13 = sc.nextLine().trim();
            System.out.print("  New Link: ");
            String link = sc.nextLine().trim();
            System.out.print("  New Pages ("+old.getPages()+"): ");
            int pages = Integer.parseInt(sc.nextLine().trim());
            System.out.print("  New Rating ("+old.getRating()+"): ");
            BigDecimal rating = new BigDecimal(sc.nextLine().trim());
            System.out.print("  New Reviews ("+old.getReviews()+"): ");
            int reviews = Integer.parseInt(sc.nextLine().trim());
            System.out.print("  New Total Ratings ("+old.getTotalRatings()+"): ");
            int totalRatings = Integer.parseInt(sc.nextLine().trim());

            Book updated = new Book(id, author, bookFormat, desc,
                    genre, img, isbn, isbn13,
                    link, pages, rating, reviews,
                    title, totalRatings);
            if (bookDao.update(updated)) {
                System.out.println("Book updated.");
            } else {
                System.out.println("Failed to update.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input.");
        }
    }

    private void deleteBook() {
        System.out.print("  Book ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        if (bookDao.delete(id)) {
            System.out.println("Book deleted.");
        } else {
            System.out.println("Book not found.");
        }
    }

    private void printBookSummary(Book b) {
        System.out.printf("Author: %s | Title: %s | Genre: %s | Format: %s | Image: %s%n",
                b.getAuthor(),
                b.getTitle(),
                b.getGenre(),
                b.getBookFormat(),
                b.getImgUrl()
        );
        System.out.printf("Title : %s%n", b.getTitle());
        System.out.printf("Total Ratings: %d%n", b.getTotalRatings());
        System.out.println("----------------------------------------");
    }


    private void searchByTitle() {
        System.out.print("  Title keyword: ");
        String kw = sc.nextLine().trim();
        List<Book> list = bookDao.searchByTitle(kw);
        if (list.isEmpty()) {
            System.out.println("No matches.");
        } else {
            list.forEach(this::printBookSummary);
        }
    }

    private void searchByAuthor() {
        System.out.print("  Author keyword: ");
        String kw = sc.nextLine().trim();
        List<Book> list = bookDao.searchByAuthor(kw);
        if (list.isEmpty()) {
            System.out.println("No matches.");
        } else {
            list.forEach(this::printBookSummary);
        }
    }

    private void displayAllBooks() {
        List<Book> all = bookDao.findAll();
        if (all.isEmpty()) {
            System.out.println("No books.");
        } else {
            all.forEach(this::printBookSummary);
        }
    }

    // === User operations (Admin only) ===
    private void addUser() {
        System.out.print("  New User ID: ");
        String id = sc.nextLine().trim();
        System.out.print("  Name: ");
        String name = sc.nextLine().trim();
        System.out.print("  Password: ");
        String pwd = sc.nextLine().trim();
        System.out.print("  Role (BORROWER/ADMIN): ");
        String rs = sc.nextLine().trim().toUpperCase();
        Person.Role role;
        try {
            role = Person.Role.valueOf(rs);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid role."); return;
        }
        Person u = role==Person.Role.ADMIN
                ? new Admin(id,name,pwd)
                : new Borrower(id,name,pwd);
        if (userDao.insert(u)) {
            System.out.println("User added.");
        } else {
            System.out.println("Failed to add user.");
        }
    }

    private void updateUser() {
        System.out.print("  User ID: ");
        String id = sc.nextLine().trim();
        Optional<Person> op = userDao.findById(id);
        if (!op.isPresent()) {
            System.out.println("User not found."); return;
        }
        Person old = op.get();
        System.out.print("  New Name ("+old.getName()+"): ");
        String name = sc.nextLine().trim();
        System.out.print("  New Password: ");
        String pwd = sc.nextLine().trim();
        System.out.print("  New Role (BORROWER/ADMIN): ");
        String rs = sc.nextLine().trim().toUpperCase();
        Person.Role role;
        try {
            role = Person.Role.valueOf(rs);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid role."); return;
        }
        Person u = role==Person.Role.ADMIN
                ? new Admin(id,name,pwd)
                : new Borrower(id,name,pwd);
        if (userDao.update(u)) {
            System.out.println("User updated.");
        } else {
            System.out.println("Failed to update user.");
        }
    }

    private void deleteUser() {
        System.out.print("  User ID: ");
        String id = sc.nextLine().trim();
        if (userDao.delete(id)) {
            System.out.println("User deleted.");
        } else {
            System.out.println("User not found.");
        }
    }

    private void displayAllUsers() {
        List<Person> all = userDao.findAll();
        if (all.isEmpty()) {
            System.out.println("No users.");
        } else {
            all.forEach(u -> System.out.printf("  %s (%s) – %s%n",
                    u.getId(), u.getName(), u.getRole()));
        }
    }

    // === Borrower-only ===
    private void viewProfile() {
        System.out.printf("  ID: %s%n  Name: %s%n  Role: %s%n",
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getRole());
    }

}
