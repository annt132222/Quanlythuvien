import org.junit.*;
import uet.group1.librarymanagement.Entities.Book;
import uet.group1.librarymanagement.Service.BookService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class BookServiceTest {

    private BookService bookService;

    @Before
    public void setUp() {
        bookService = new BookService();
        List<Book> all = bookService.findAllBooks();
        for (Book b : all) {
            bookService.deleteBook(b.getId());
        }
    }

    @Test
    public void addBook_shouldReturnTrue_andBeFindable() {
        Book b = new Book(
                100001,
                "Nguyễn Nhật Ánh",         // author
                "Hardcover",               // format
                "Truyện thiếu nhi",        // desc
                "Fiction",                 // genre
                "http://img",              // imgUrl
                "ISBN123",                 // isbn
                "ISBN13-123",              // isbn13
                "http://link",             // link
                200,                       // pages
                new BigDecimal("4.75"),    // rating
                1500,                      // reviews
                "Cho tôi xin một vé về tuổi thơ", // title
                20000                      // totalRatings
        );

        boolean added = bookService.addBook(b);
        assertTrue("addBook phải trả về true khi thêm thành công", added);

        Optional<Book> found = bookService.findByIsbn("ISBN123");
        assertTrue("findByIsbn phải tìm thấy cuốn vừa thêm", found.isPresent());
        assertEquals("Title phải khớp", "Cho tôi xin một vé về tuổi thơ", found.get().getTitle());
    }

    @Test
    public void updateBook_shouldModifyFields() {
        // Thêm sách
        Book b = new Book(100002, "A", "F", "D", "G","", "UPISBN","", "",10,
                new BigDecimal("3.50"),5,"Old Title",100);
        assertTrue(bookService.addBook(b));
        int id = bookService.findByIsbn("UPISBN").get().getId();

        // Cập nhật
        Book updated = new Book(
                id,
                "Updated Author",
                "Ebook",
                "New Desc",
                "Drama",
                "http://newimg",
                "UPISBN",
                "UPISBN13",
                "http://newlink",
                300,
                new BigDecimal("4.20"),
                999,
                "New Title",
                5000
        );
        boolean ok = bookService.updateBook(updated);
        assertTrue("updateBook phải trả về true khi cập nhật thành công", ok);

        Book b2 = bookService.findByIsbn("UPISBN").get();
        assertEquals("Author phải được cập nhật", "Updated Author", b2.getAuthor());
        assertEquals("Title phải được cập nhật", "New Title", b2.getTitle());
        assertEquals("Pages phải được cập nhật", 300, b2.getPages());
    }

    @Test
    public void deleteBook_shouldRemoveBookAndReturnFalseWhenFind() {
        Book b = new Book(100003,"X","F","D","G","", "DELISBN","","",5,
                new BigDecimal("2.00"),0,"ToDelete",0);
        assertTrue(bookService.addBook(b));
        int id = bookService.findByIsbn("DELISBN").get().getId();

        boolean delOk = bookService.deleteBook(id);
        assertTrue("deleteBook phải trả về true khi xóa thành công", delOk);

        Optional<Book> after = bookService.findByIsbn("DELISBN");
        assertFalse("Sau khi xóa, findByIsbn không nên tìm thấy", after.isPresent());
    }
}

