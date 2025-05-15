import org.junit.*;
import uet.group1.librarymanagement.Entities.Book;
import uet.group1.librarymanagement.Entities.BorrowRecord;
import uet.group1.librarymanagement.Entities.Borrower;
import uet.group1.librarymanagement.Entities.Person;
import uet.group1.librarymanagement.Service.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class BorrowServiceTest {

    private UserService   userService;
    private BookService   bookService;
    private BorrowService borrowService;

    @Before
    public void setUp() {
        userService   = new UserService();
        bookService   = new BookService();
        borrowService = new BorrowService();

        // Xóa người cũ nếu có
        userService.findAllUsers().forEach(u -> userService.deleteUser(u.getId()));
        // Tạo borrower test
        Person br = new Borrower("u100","UnitTester","pwd123");
        assertTrue("addUser phải thành công", userService.addUser(br));

        // Xóa sách & record cũ
        bookService.findAllBooks().forEach(b -> bookService.deleteBook(b.getId()));
    }

    @Test
    public void borrowEmptyIsbn_shouldReturnFalse() {
        boolean ok = borrowService.borrowByIsbn("u100", "");
        assertFalse("Không thể mượn khi ISBN rỗng", ok);
    }

    @Test
    public void borrowNonexistentIsbn_shouldReturnFalse() {
        boolean ok = borrowService.borrowByIsbn("u100", "NOISBN");
        assertFalse("Không thể mượn khi ISBN không tồn tại", ok);
    }

    @Test
    public void borrowAndReturnFlow_shouldWork() {
        // Thêm sách
        Book b = new Book(
                0, "Auth", "Hardcover", "D", "SciFi",
                "", "BOR123", "BOR13", "", 120,
                new BigDecimal("4.00"), 10, "Borrowable", 1000
        );
        assertTrue(bookService.addBook(b));
        String isbn = "BOR123";
        int bookId = bookService.findByIsbn(isbn).get().getId();

        // Mượn
        boolean borrowed = borrowService.borrowByIsbn("u100", isbn);
        assertTrue("borrowByIsbn phải true khi mượn thành công", borrowed);

        // Danh sách loans phải có đúng 1 record với returnDate == null
        List<BorrowRecord> loans = borrowService.listCurrentLoans("u100");
        assertEquals(1, loans.size());
        BorrowRecord rec = loans.get(0);
        assertEquals("BookId trong record phải khớp", bookId, rec.getBookId());
        assertNull("returnDate phải null (chưa trả)", rec.getReturnDate());

        // Trả
        boolean returned = borrowService.returnByIsbn("u100", isbn);
        assertTrue("returnByIsbn phải true khi trả thành công", returned);

        // Sau trả, không còn loan nào
        List<BorrowRecord> after = borrowService.listCurrentLoans("u100");
        assertTrue("Sau khi trả, không còn record nào", after.isEmpty());

        // Nếu gọi trả lại lần nữa cũng phải false
        assertFalse("Trả lại lần 2 phải false", borrowService.returnByIsbn("u100", isbn));
    }
}

