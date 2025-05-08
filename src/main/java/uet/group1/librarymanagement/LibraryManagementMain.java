package uet.group1.librarymanagement;


import uet.group1.librarymanagement.Entities.Book;
import uet.group1.librarymanagement.Entities.Borrower;
import uet.group1.librarymanagement.Utils.LibUtils;
import uet.group1.librarymanagement.Exceptions.*;

import java.util.List;
import java.util.Scanner;

public class LibraryManagementMain {
    private static final LibUtils lib = new LibUtils();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            showMenu();
            String cmd = sc.nextLine().trim();
            switch (cmd) {
                case "0": return;
                case "1": addBook();         break;
                case "2": removeBook();      break;
                case "3": updateBook();      break;
                case "4": findBook();        break;
                case "5": lib.displayAllBooks(); break;
                case "6": addBorrower();     break;
                case "7": borrowBook();      break;
                case "8": returnBook();      break;
                case "9": displayBorrower(); break;
                default: System.out.println("Action not supported.");
            }
            System.out.println();
        }
    }

    private static void showMenu() {
        System.out.println("=== Library CLI ===");
        System.out.println("[0] Exit");
        System.out.println("[1] Add Book");
        System.out.println("[2] Remove Book");
        System.out.println("[3] Update Book");
        System.out.println("[4] Find Book");
        System.out.println("[5] Display All Books");
        System.out.println("[6] Add Borrower");
        System.out.println("[7] Borrow Book");
        System.out.println("[8] Return Book");
        System.out.println("[9] Display Borrower Info");
        System.out.print("Select action: ");
    }

    private static void addBook() {
        System.out.print("  ID: ");
        String id = sc.nextLine();
        System.out.print("  Title: ");
        String title = sc.nextLine();
        System.out.print("  Author: ");
        String author = sc.nextLine();
        System.out.print("  Quantity: ");
        int qty = Integer.parseInt(sc.nextLine());
        if (lib.addBook(new Book(id, title, author, qty))) {
            System.out.println("  Book added.");
        } else {
            System.out.println("  ID already exists or invalid.");
        }
    }

    private static void removeBook() {
        System.out.print("  Book ID: ");
        if (lib.removeBook(sc.nextLine())) {
            System.out.println("  Book removed.");
        } else {
            System.out.println("  Book not found.");
        }
    }

    private static void updateBook() {
        System.out.print("  Book ID: ");
        String id = sc.nextLine();
        System.out.print("  New title: ");
        String title = sc.nextLine();
        System.out.print("  New author: ");
        String author = sc.nextLine();
        System.out.print("  New quantity: ");
        int qty = Integer.parseInt(sc.nextLine());
        if (lib.updateBook(id, title, author, qty)) {
            System.out.println("  Book updated.");
        } else {
            System.out.println("  Book not found.");
        }
    }

    private static void findBook() {
        System.out.print("  Keyword: ");
        List<Book> results = lib.findBooks(sc.nextLine());
        if (results.isEmpty()) {
            System.out.println("  No matches.");
        } else {
            results.forEach(book -> System.out.println("  " + book));
        }
    }

    private static void addBorrower() {
        System.out.print("  Borrower ID: ");
        String id = sc.nextLine();
        System.out.print("  Name: ");
        String name = sc.nextLine();
        Borrower b = new Borrower(id, name);
        if (lib.addBorrower(b)) {
            System.out.println("  Borrower added.");
        } else {
            System.out.println("  ID already exists or invalid.");
        }
    }

    private static void borrowBook() {
        System.out.print("  Borrower ID: ");
        String bid = sc.nextLine();
        System.out.print("  Book ID: ");
        String bookId = sc.nextLine();
        try {
            lib.borrowBook(bid, bookId);
            System.out.println("  Borrow successful.");
        } catch (InvalidInputException |
                 UserNotFoundException |
                 BookNotFoundException |
                 OutOfStockException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
    }

    private static void returnBook() {
        System.out.print("  Borrower ID: ");
        String bid = sc.nextLine();
        System.out.print("  Book ID: ");
        String bookId = sc.nextLine();
        try {
            lib.returnBook(bid, bookId);
            System.out.println("  Return successful.");
        } catch (InvalidInputException |
                 UserNotFoundException |
                 BookNotFoundException |
                 NotBorrowedException e) {
            System.out.println("  ERROR: " + e.getMessage());
        }
    }

    private static void displayBorrower() {
        System.out.print("  Borrower ID: ");
        lib.displayBorrowerInfo(sc.nextLine());
    }
}
