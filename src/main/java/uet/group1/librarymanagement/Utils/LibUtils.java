package uet.group1.librarymanagement.Utils;

import uet.group1.librarymanagement.Entities.Book;
import uet.group1.librarymanagement.Entities.Borrower;
import uet.group1.librarymanagement.Exceptions.BookNotFoundException;
import uet.group1.librarymanagement.Exceptions.InvalidInputException;
import uet.group1.librarymanagement.Exceptions.NotBorrowedException;
import uet.group1.librarymanagement.Exceptions.OutOfStockException;
import uet.group1.librarymanagement.Exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for managing library operations:
 * - books (add / remove / update / search / list)
 * - borrowers (add / info)
 * - borrow & return
 */
public class LibUtils {
    private final Map<String, Book>    books     = new HashMap<>();
    private final Map<String, Borrower> borrowers = new HashMap<>();

    /**
     * Adds a new book to the library.
     *
     * @param book the Book to add
     * @return true if added; false if null or ID already exists
     */
    public boolean addBook(Book book) {
        if (book == null || books.containsKey(book.getId())) {
            return false;
        }
        books.put(book.getId(), book);
        return true;
    }

    /**
     * Removes a book by its ID.
     *
     * @param bookId the ID of the book to remove
     * @return true if removed; false if not found
     */
    public boolean removeBook(String bookId) {
        return books.remove(bookId) != null;
    }

    /**
     * Updates an existing book's data.
     *
     * @param bookId   the ID of the book
     * @param title    new title
     * @param author   new author
     * @param quantity new available quantity
     * @return true if updated; false if book not found
     */
    public boolean updateBook(String bookId, String title, String author, int quantity) {
        Book book = books.get(bookId);
        if (book == null) {
            return false;
        }
        book.setTitle(title);
        book.setAuthor(author);
        book.setQuantity(quantity);
        return true;
    }

    /**
     * Finds books whose title or author contains the keyword (case-insensitive).
     *
     * @param keyword the search term
     * @return list of matching books (empty if none or if keyword is null/empty)
     */
    public List<Book> findBooks(String keyword) {
        List<Book> result = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }
        String kw = keyword.trim().toLowerCase();
        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase().contains(kw)
                    || book.getAuthor().toLowerCase().contains(kw)) {
                result.add(book);
            }
        }
        return result;
    }

    /**
     * Displays all books to standard output.
     */
    public void displayAllBooks() {
        if (books.isEmpty()) {
            System.out.println("No books in library.");
            return;
        }
        books.values().forEach(book -> System.out.println("  " + book));
    }

    /**
     * Adds a new borrower to the system.
     *
     * @param borrower the Borrower to add
     * @return true if added; false if null or ID already exists
     */
    public boolean addBorrower(Borrower borrower) {
        if (borrower == null || borrowers.containsKey(borrower.getId())) {
            return false;
        }
        borrowers.put(borrower.getId(), borrower);
        return true;
    }

    /**
     * Displays information about a borrower by their ID.
     *
     * @param borrowerId the ID of the borrower
     */
    public void displayBorrowerInfo(String borrowerId) {
        Borrower b = borrowers.get(borrowerId);
        if (b == null) {
            System.out.println("  Borrower not found: " + borrowerId);
        } else {
            System.out.println(b);
        }
    }

    /**
     * Borrows a book for a borrower.
     *
     * @param borrowerId the borrower's ID
     * @param bookId     the book's ID
     * @throws InvalidInputException if any ID is null or empty
     * @throws UserNotFoundException if borrower does not exist
     * @throws BookNotFoundException if book does not exist
     * @throws OutOfStockException   if no copies are available
     */
    public void borrowBook(String borrowerId, String bookId)
            throws InvalidInputException,
            UserNotFoundException,
            BookNotFoundException,
            OutOfStockException {

        if (isNullOrEmpty(borrowerId) || isNullOrEmpty(bookId)) {
            throw new InvalidInputException("Borrower ID and Book ID must not be empty.");
        }

        Borrower b = borrowers.get(borrowerId);
        if (b == null) {
            throw new UserNotFoundException(borrowerId);
        }

        Book book = books.get(bookId);
        if (book == null) {
            throw new BookNotFoundException(bookId);
        }

        if (book.getQuantity() <= 0) {
            throw new OutOfStockException(bookId);
        }

        book.setQuantity(book.getQuantity() - 1);
        b.borrow(bookId);
    }

    /**
     * Returns a book from a borrower.
     *
     * @param borrowerId the borrower's ID
     * @param bookId     the book's ID
     * @throws InvalidInputException  if any ID is null or empty
     * @throws UserNotFoundException  if borrower does not exist
     * @throws BookNotFoundException  if book does not exist
     * @throws NotBorrowedException   if borrower did not borrow this book
     */
    public void returnBook(String borrowerId, String bookId)
            throws InvalidInputException,
            UserNotFoundException,
            BookNotFoundException,
            NotBorrowedException {

        if (isNullOrEmpty(borrowerId) || isNullOrEmpty(bookId)) {
            throw new InvalidInputException("Borrower ID and Book ID must not be empty.");
        }

        Borrower b = borrowers.get(borrowerId);
        if (b == null) {
            throw new UserNotFoundException(borrowerId);
        }

        Book book = books.get(bookId);
        if (book == null) {
            throw new BookNotFoundException(bookId);
        }

        if (!b.returnBook(bookId)) {
            throw new NotBorrowedException(bookId);
        }

        book.setQuantity(book.getQuantity() + 1);
    }

    /**
     * Helper: check for null or empty (after trim).
     */
    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
