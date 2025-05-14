package uet.group1.librarymanagement.dao;

import uet.group1.librarymanagement.Entities.Book;
import uet.group1.librarymanagement.Utils.ConnectorDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDaoImpl implements BookDao {
    @Override
    public Optional<Book> findById(Integer id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY id";
        try (Connection c = ConnectorDB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean insert(Book b) {
        String sql = "INSERT INTO books(author, bookformat, `desc`, genre, img, isbn, isbn13, link,"
                + " pages, rating, reviews, title, totalratings) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, b.getAuthor());
            ps.setString(i++, b.getBookFormat());
            ps.setString(i++, b.getDescription());
            ps.setString(i++, b.getGenre());
            ps.setString(i++, b.getImgUrl());
            ps.setString(i++, b.getIsbn());
            ps.setString(i++, b.getIsbn13());
            ps.setString(i++, b.getLink());
            ps.setInt(i++, b.getPages());
            ps.setBigDecimal(i++, b.getRating());
            ps.setInt(i++, b.getReviews());
            ps.setString(i++, b.getTitle());
            ps.setInt(i,   b.getTotalRatings());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Book b) {
        String sql = "UPDATE books SET author=?, bookformat=?, `desc`=?, genre=?, img=?, isbn=?, isbn13=?, link=?,"
                + " pages=?, rating=?, reviews=?, title=?, totalratings=? WHERE id=?";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int i = 1;
            ps.setString(i++, b.getAuthor());
            ps.setString(i++, b.getBookFormat());
            ps.setString(i++, b.getDescription());
            ps.setString(i++, b.getGenre());
            ps.setString(i++, b.getImgUrl());
            ps.setString(i++, b.getIsbn());
            ps.setString(i++, b.getIsbn13());
            ps.setString(i++, b.getLink());
            ps.setInt(i++, b.getPages());
            ps.setBigDecimal(i++, b.getRating());
            ps.setInt(i++, b.getReviews());
            ps.setString(i++, b.getTitle());
            ps.setInt(i++, b.getTotalRatings());
            ps.setInt(i,   b.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM books WHERE id=?";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Book> searchByTitle(String title) {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? ORDER BY id";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + title + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Book> searchByAuthor(String author) {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE author LIKE ? ORDER BY id";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + author + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ? OR isbn13 = ?";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, isbn);
            ps.setString(2, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }


    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
                rs.getInt("id"),
                rs.getString("author"),
                rs.getString("bookformat"),
                rs.getString("desc"),
                rs.getString("genre"),
                rs.getString("img"),
                rs.getString("isbn"),
                rs.getString("isbn13"),
                rs.getString("link"),
                rs.getInt("pages"),
                rs.getBigDecimal("rating"),
                rs.getInt("reviews"),
                rs.getString("title"),
                rs.getInt("totalratings")
        );
    }
}
