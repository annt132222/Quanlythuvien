package uet.group1.librarymanagement.dao;

import uet.group1.librarymanagement.Entities.Book;
import java.util.List;

public interface BookDao extends Dao<Book, Integer> {
    /**
     * Find books matching title (partial match).
     */
    List<Book> searchByTitle(String title);

    /**
     * Find books matching author (partial match).
     */
    List<Book> searchByAuthor(String author);

}
