package uet.group1.librarymanagement.Exceptions;

public class NotBorrowedException extends Exception {
    public NotBorrowedException(String bookId) {
        super("Cannot return—book not borrowed: " + bookId);
    }
}
