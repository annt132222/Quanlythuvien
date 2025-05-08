package uet.group1.librarymanagement.Entities;

public class Book {
    private String id;
    private String title;
    private String author;
    private int quantity;

    public Book(String id, String title, String author, int quantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.quantity = quantity;
    }

    public String getId() { return this.id; }
    public String getTitle() { return this.title; }
    public String getAuthor() { return this.author; }
    public int getQuantity() { return this.quantity; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return String.format("[%s] \"%s\" by %s (Available: %d)",
                id, title, author, quantity);
    }
}
