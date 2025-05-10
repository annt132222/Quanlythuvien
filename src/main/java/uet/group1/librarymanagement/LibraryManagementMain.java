package uet.group1.librarymanagement;


import uet.group1.librarymanagement.dao.BookDao;
import uet.group1.librarymanagement.dao.UserDao;
import uet.group1.librarymanagement.dao.BookDaoImpl;
import uet.group1.librarymanagement.dao.UserDaoImpl;
import uet.group1.librarymanagement.Entities.Book;
import uet.group1.librarymanagement.Entities.Borrower;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class LibraryManagementMain {
    private static final Scanner sc = new Scanner(System.in);
    private static final BookDao bookDao = new BookDaoImpl();
    private static final UserDao userDao = new UserDaoImpl();

    public static void main(String[] args) {
        while (true) {
            showMenu();
            String cmd = sc.nextLine().trim();
            switch (cmd) {
                case "0": return;
                case "1": addBook();          break;
                case "2": removeBook();       break;
                case "3": updateBook();       break;
                case "4": findBook();         break;
                case "5": displayAllBooks();  break;
                case "6": addBorrower();      break;
                case "7": displayBorrower();  break;
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
        System.out.println("[7] Display Borrower Info");
        System.out.print("Select action: ");
    }

    private static void addBook() {
        System.out.print("  Author: ");
        String author = sc.nextLine();
        System.out.print("  Format: ");
        String bookFormat = sc.nextLine();
        System.out.print("  Description: ");
        String description = sc.nextLine();
        System.out.print("  Genre: ");
        String genre = sc.nextLine();
        System.out.print("  Image URL: ");
        String imgUrl = sc.nextLine();
        System.out.print("  ISBN: ");
        String isbn = sc.nextLine();
        System.out.print("  ISBN13: ");
        String isbn13 = sc.nextLine();
        System.out.print("  Link: ");
        String link = sc.nextLine();
        System.out.print("  Pages: ");
        int pages = Integer.parseInt(sc.nextLine());
        System.out.print("  Rating: ");
        BigDecimal rating = new BigDecimal(sc.nextLine());
        System.out.print("  Reviews: ");
        int reviews = Integer.parseInt(sc.nextLine());
        System.out.print("  Title: ");
        String title = sc.nextLine();
        System.out.print("  Total Ratings: ");
        int totalRatings = Integer.parseInt(sc.nextLine());

        Book book = new Book(
                0, author, bookFormat, description, genre,
                imgUrl, isbn, isbn13, link, pages,
                rating, reviews, title, totalRatings
        );
        if (bookDao.insert(book)) {
            System.out.println("  Book added.");
        } else {
            System.out.println("  Failed to add book.");
        }
    }

    private static void removeBook() {
        System.out.print("  Book ID: ");
        int id = Integer.parseInt(sc.nextLine());
        if (bookDao.delete(id)) {
            System.out.println("  Book removed.");
        } else {
            System.out.println("  Book not found.");
        }
    }

    private static void updateBook() {
        System.out.print("  Book ID: ");
        int id = Integer.parseInt(sc.nextLine());
        Optional<Book> opt = bookDao.findById(id);
        if (!opt.isPresent()) {
            System.out.println("  Book not found.");
            return;
        }
        Book existing = opt.get();
        System.out.printf("  Old Title [%s]: ", existing.getTitle());
        String title = sc.nextLine();
        existing.setTitle(title.isEmpty() ? existing.getTitle() : title);
        // Similar prompts for other fields...
        // For brevity, only title updated here
        if (bookDao.update(existing)) {
            System.out.println("  Book updated.");
        } else {
            System.out.println("  Failed to update.");
        }
    }

    private static void findBook() {
        System.out.print("  Keyword: ");
        String kw = sc.nextLine();
        List<Book> found = bookDao.searchByTitleOrAuthor(kw);
        if (found.isEmpty()) {
            System.out.println("  No matches.");
        } else {
            found.forEach(b -> System.out.println("  " + b));
        }
    }

    private static void displayAllBooks() {
        List<Book> all = bookDao.findAll();
        if (all.isEmpty()) {
            System.out.println("  No books in database.");
        } else {
            all.forEach(b -> System.out.println("  " + b));
        }
    }

    private static void addBorrower() {
        System.out.print("  Borrower ID: ");
        String id = sc.nextLine();
        System.out.print("  Name: ");
        String name = sc.nextLine();
        Borrower u = new Borrower(id, name);
        if (userDao.insert(u)) {
            System.out.println("  Borrower added.");
        } else {
            System.out.println("  Failed to add borrower.");
        }
    }

    private static void displayBorrower() {
        System.out.print("  Borrower ID: ");
        String id = sc.nextLine();
        Optional<Borrower> opt = userDao.findById(id);
        if (opt.isPresent()) {
            System.out.println(opt.get());
        } else {
            System.out.println("  Borrower not found.");
        }
    }}
