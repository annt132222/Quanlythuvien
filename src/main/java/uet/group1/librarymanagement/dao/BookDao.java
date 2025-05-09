package uet.group1.librarymanagement.dao;

import uet.group1.librarymanagement.Entities.Book;
import java.util.List;

public interface BookDao extends Dao<Book, Integer> {
    List<Book> searchByTitleOrAuthor(String keyword);
}
