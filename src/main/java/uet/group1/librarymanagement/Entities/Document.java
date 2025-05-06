package uet.group1.librarymanagement.Entities;

public abstract class Document {
    private static int counter = 1;
    private final int id;
    private String title;
    private String author;
    private boolean borrowed;

    public Document(String title, String author) {
        this.id = counter++;
        this.title = title;
        this.author = author;
        this.borrowed = false;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public boolean isBorrowed() { return borrowed; }
    public void setBorrowed(boolean borrowed) { this.borrowed = borrowed; }

    @Override
    public String toString() {
        return String.format("[%d] \"%s\" – %s [%s]",
                id, title, author, borrowed ? "Đã mượn" : "Sẵn sàng");
    }
}
