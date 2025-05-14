package uet.group1.librarymanagement.dao;

import uet.group1.librarymanagement.Entities.BorrowRecord;
import uet.group1.librarymanagement.Utils.ConnectorDB;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of BorrowRecordDao, using ConnectorDB for connections.
 */
public class BorrowRecordDaoImpl implements BorrowRecordDao {

    @Override
    public Optional<BorrowRecord> findById(Integer id) {
        String sql = "SELECT * FROM borrow_records WHERE id = ?";
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
    public List<BorrowRecord> findAll() {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records ORDER BY id";
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
    public boolean insert(BorrowRecord r) {
        String sql = "INSERT INTO borrow_records(user_id, book_id, borrow_date) VALUES (?, ?, ?)";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getUserId());
            ps.setInt(2, r.getBookId());
            ps.setTimestamp(3, Timestamp.valueOf(r.getBorrowDate()));
            if (ps.executeUpdate() == 1) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        r.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(BorrowRecord r) {
        String sql = "UPDATE borrow_records SET return_date = ? WHERE id = ?";
        if (r.getReturnDate() == null) {
            return false;
        }
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(r.getReturnDate()));
            ps.setInt(2, r.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM borrow_records WHERE id = ?";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<BorrowRecord> findActiveRecord(String userId, int bookId) {
        String sql = "SELECT * FROM borrow_records WHERE user_id = ? AND book_id = ? AND return_date IS NULL";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, bookId);
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
    public List<BorrowRecord> findUnreturnedByUser(String userId) {
        List<BorrowRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE user_id = ? AND return_date IS NULL";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
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

    /** Map a ResultSet row to a BorrowRecord entity */
    private BorrowRecord mapRow(ResultSet rs) throws SQLException {
        Timestamp borrowTs = rs.getTimestamp("borrow_date");
        Timestamp returnTs = rs.getTimestamp("return_date");
        return new BorrowRecord(
                rs.getInt("id"),
                rs.getString("user_id"),
                rs.getInt("book_id"),
                borrowTs.toLocalDateTime(),
                returnTs != null ? returnTs.toLocalDateTime() : null
        );
    }
}
