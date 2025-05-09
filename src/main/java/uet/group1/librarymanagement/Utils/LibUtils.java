package uet.group1.librarymanagement.Utils;

import uet.group1.librarymanagement.Entities.Book;
import uet.group1.librarymanagement.Entities.Borrower;
import uet.group1.librarymanagement.Exceptions.BookNotFoundException;
import uet.group1.librarymanagement.Exceptions.InvalidInputException;
import uet.group1.librarymanagement.Exceptions.NotBorrowedException;
import uet.group1.librarymanagement.Exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private final Map<Integer, Book>    books     = new HashMap<>();
    private final Map<String, Borrower> borrowers = new HashMap<>();

    /**
     * Adds a new book. Book ID must be > 0 and not exist.
     */
    public boolean addBook(Book book) {
        if (book == null || book.getId() <= 0 || books.containsKey(book.getId())) {
            return false;
        }
        books.put(book.getId(), book);
        return true;
    }

    /**
     * Removes a book by its ID.
     */
    public boolean removeBook(int bookId) {
        return books.remove(bookId) != null;
    }

    /**
     * Updates an existing book (matching by ID).
     */
    public boolean updateBook(Book updated) {
        if (updated == null || !books.containsKey(updated.getId())) {
            return false;
        }
        books.put(updated.getId(), updated);
        return true;
    }

    /**
     * Searches books by title, author, or genre (case-insensitive).
     */
    public List<Book> findBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String kw = keyword.trim().toLowerCase();
        List<Book> result = new ArrayList<>();
        for (Book b : books.values()) {
            boolean match = b.getTitle().toLowerCase().contains(kw)
                    || b.getAuthor().toLowerCase().contains(kw);
            String genre = b.getGenre();
            if (!match && genre != null && genre.toLowerCase().contains(kw)) {
                match = true;
            }
            if (match) {
                result.add(b);
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
        getBooks(0, books.size()).forEach(b -> System.out.println("  " + b));
    }

    /**
     * Returns total number of books.
     */
    public int getTotalBooks() {
        return books.size();
    }

    /**
     * Returns a sublist of books sorted by ID, starting at index start (0-based),
     * up to count elements.
     */
    public List<Book> getBooks(int start, int count) {
        List<Book> sorted = new ArrayList<>(books.values());
        sorted.sort(Comparator.comparingInt(Book::getId));
        if (start < 0 || start >= sorted.size()) {
            return Collections.emptyList();
        }
        int end = Math.min(start + count, sorted.size());
        return sorted.subList(start, end);
    }

    /**
     * Displays a subset of books from start index, up to count elements.
     */
    public void displayBooks(int start, int count) {
        List<Book> subset = getBooks(start, count);
        if (subset.isEmpty()) {
            System.out.println("No books to display.");
            return;
        }
        subset.forEach(b -> System.out.println("  " + b));
    }

    /**
     * Adds a new borrower.
     */
    public boolean addBorrower(Borrower borrower) {
        if (borrower == null || isNullOrEmpty(borrower.getId()) || borrowers.containsKey(borrower.getId())) {
            return false;
        }
        borrowers.put(borrower.getId(), borrower);
        return true;
    }

    /**
     * Displays borrower information by ID.
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
     * Records borrowing a book by a borrower.
     */
    public void borrowBook(String borrowerId, int bookId)
            throws InvalidInputException,
            UserNotFoundException,
            BookNotFoundException {
        if (isNullOrEmpty(borrowerId)) {
            throw new InvalidInputException("Borrower ID must not be empty.");
        }
        Borrower br = borrowers.get(borrowerId);
        if (br == null) {
            throw new UserNotFoundException(borrowerId);
        }
        Book bk = books.get(bookId);
        if (bk == null) {
            throw new BookNotFoundException(String.valueOf(bookId));
        }
        br.borrow(bookId);
    }

    /**
     * Records returning a book by a borrower.
     */
    public void returnBook(String borrowerId, int bookId)
            throws InvalidInputException,
            UserNotFoundException,
            BookNotFoundException,
            NotBorrowedException {
        if (isNullOrEmpty(borrowerId)) {
            throw new InvalidInputException("Borrower ID must not be empty.");
        }
        Borrower br = borrowers.get(borrowerId);
        if (br == null) {
            throw new UserNotFoundException(borrowerId);
        }
        Book bk = books.get(bookId);
        if (bk == null) {
            throw new BookNotFoundException(String.valueOf(bookId));
        }
        if (!br.returnBook(bookId)) {
            throw new NotBorrowedException(String.valueOf(bookId));
        }
    }

    /**
     * Helper: check for null or empty string.
     */
    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
