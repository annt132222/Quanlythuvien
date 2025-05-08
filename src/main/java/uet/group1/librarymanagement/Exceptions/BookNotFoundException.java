package uet.group1.librarymanagement.Exceptions;

public class BookNotFoundException extends Exception {
    public BookNotFoundException(String bookId) {
        super("Book not found: " + bookId);
    }
}
