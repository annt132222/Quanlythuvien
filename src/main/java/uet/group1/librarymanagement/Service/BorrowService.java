package uet.group1.librarymanagement.Service;

import uet.group1.librarymanagement.dao.BookDao;
import uet.group1.librarymanagement.dao.BorrowRecordDao;
import uet.group1.librarymanagement.dao.UserDao;
import uet.group1.librarymanagement.dao.BookDaoImpl;
import uet.group1.librarymanagement.dao.BorrowRecordDaoImpl;
import uet.group1.librarymanagement.dao.UserDaoImpl;
import uet.group1.librarymanagement.Entities.Book;
import uet.group1.librarymanagement.Entities.BorrowRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BorrowService {
    private final UserDao userDao           = new UserDaoImpl();
    private final BookDao bookDao           = new BookDaoImpl();
    private final BorrowRecordDao recDao    = new BorrowRecordDaoImpl();

    /** Mượn theo ISBN, trả về true nếu thành công */
    public boolean borrowByIsbn(String userId, String isbn) {
        // 1. user tồn tại?
        if (!userDao.findById(userId).isPresent()) return false;
        // 2. tìm book
        Optional<Book> ob = bookDao.findByIsbn(isbn);
        if (!ob.isPresent()) return false;
        int bookId = ob.get().getId();
        // 3. chưa mượn trước đó?
        if (recDao.findActiveRecord(userId, bookId).isPresent()) return false;
        // 4. tạo record
        BorrowRecord r = new BorrowRecord(0, userId, bookId, LocalDateTime.now(), null);
        return recDao.insert(r);
    }

    /** Trả theo ISBN, trả về true nếu thành công */
    public boolean returnByIsbn(String userId, String isbn) {
        if (!userDao.findById(userId).isPresent()) return false;
        Optional<Book> ob = bookDao.findByIsbn(isbn);
        if (!ob.isPresent()) return false;
        int bookId = ob.get().getId();
        Optional<BorrowRecord> or = recDao.findActiveRecord(userId, bookId);
        if (!or.isPresent()) return false;
        BorrowRecord r = or.get();
        r.setReturnDate(LocalDateTime.now());
        return recDao.update(r);
    }

    /** Danh sách sách đang mượn (returnDate == null) */
    public List<BorrowRecord> listCurrentLoans(String userId) {
        return recDao.findUnreturnedByUser(userId);
    }
}
