package uet.group1.librarymanagement.Service;

import uet.group1.librarymanagement.dao.BookDao;
import uet.group1.librarymanagement.dao.BookDaoImpl;
import uet.group1.librarymanagement.Entities.Book;

import java.util.List;
import java.util.Optional;

public class BookService {
    private final BookDao bookDao = new BookDaoImpl();

    /** Thêm sách mới. @return true nếu insert thành công. */
    public boolean addBook(Book b) {
        return bookDao.insert(b);
    }

    /** Cập nhật thông tin sách. @return true nếu update thành công. */
    public boolean updateBook(Book b) {
        return bookDao.update(b);
    }

    /** Xóa sách theo ID. @return true nếu delete thành công. */
    public boolean deleteBook(int id) {
        return bookDao.delete(id);
    }

    /** Tìm tất cả sách. */
    public List<Book> findAllBooks() {
        return bookDao.findAll();
    }

    /** Tìm sách theo tiêu đề (partial match). */
    public List<Book> searchByTitle(String keyword) {
        return bookDao.searchByTitle(keyword);
    }

    /** Tìm sách theo tác giả (partial match). */
    public List<Book> searchByAuthor(String keyword) {
        return bookDao.searchByAuthor(keyword);
    }

    /** Tìm sách theo ID. */
    public Optional<Book> findById(int id) {
        return bookDao.findById(id);
    }

    /** Tìm sách theo ISBN hoặc ISBN13. */
    public Optional<Book> findByIsbn(String isbn) {
        return bookDao.findByIsbn(isbn);
    }
}

