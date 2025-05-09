package uet.group1.librarymanagement.Entities;

import java.math.BigDecimal;

public class Book {
    private int     id;            // auto-increment PK
    private String  author;
    private String  bookFormat;
    private String  description;   // tương ứng với `desc`
    private String  genre;
    private String  imgUrl;        // tương ứng với img
    private String  isbn;
    private String  isbn13;
    private String  link;
    private int     pages;
    private BigDecimal rating;
    private int     reviews;
    private String  title;
    private int     totalRatings;

    // Constructor dùng khi đọc từ DB (có id)
    public Book(int id,
                String author,
                String bookFormat,
                String description,
                String genre,
                String imgUrl,
                String isbn,
                String isbn13,
                String link,
                int pages,
                BigDecimal rating,
                int reviews,
                String title,
                int totalRatings) {
        this.id           = id;
        this.author       = author;
        this.bookFormat   = bookFormat;
        this.description  = description;
        this.genre        = genre;
        this.imgUrl       = imgUrl;
        this.isbn         = isbn;
        this.isbn13       = isbn13;
        this.link         = link;
        this.pages        = pages;
        this.rating       = rating;
        this.reviews      = reviews;
        this.title        = title;
        this.totalRatings = totalRatings;
    }

    // Constructor dùng khi insert mới (không có id)
    public Book(String author,
                String bookFormat,
                String description,
                String genre,
                String imgUrl,
                String isbn,
                String isbn13,
                String link,
                int pages,
                BigDecimal rating,
                int reviews,
                String title,
                int totalRatings) {
        this(0, author, bookFormat, description, genre, imgUrl,
                isbn, isbn13, link, pages, rating, reviews, title, totalRatings);
    }

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getBookFormat() { return bookFormat; }
    public void setBookFormat(String bookFormat) { this.bookFormat = bookFormat; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getImgUrl() { return imgUrl; }
    public void setImgUrl(String imgUrl) { this.imgUrl = imgUrl; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getIsbn13() { return isbn13; }
    public void setIsbn13(String isbn13) { this.isbn13 = isbn13; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public int getReviews() { return reviews; }
    public void setReviews(int reviews) { this.reviews = reviews; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getTotalRatings() { return totalRatings; }
    public void setTotalRatings(int totalRatings) { this.totalRatings = totalRatings; }

    @Override
    public String toString() {
        return String.format(
                "[%d] %s by %s (%s) – %d pages, rating=%.2f, reviews=%d",
                id, title, author, bookFormat, pages,
                rating.doubleValue(), reviews
        );
    }
}
