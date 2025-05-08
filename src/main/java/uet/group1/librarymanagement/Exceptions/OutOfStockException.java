package uet.group1.librarymanagement.Exceptions;

public class OutOfStockException extends Exception {
    public OutOfStockException(String bookId) {
        super("Book out of stock: " + bookId);
    }
}
