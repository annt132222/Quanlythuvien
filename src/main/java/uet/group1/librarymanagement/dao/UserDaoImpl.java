package uet.group1.librarymanagement.dao;

import uet.group1.librarymanagement.dao.UserDao;
import uet.group1.librarymanagement.Entities.Borrower;
import uet.group1.librarymanagement.Utils.ConnectorDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    @Override
    public Optional<Borrower> findById(String id) {
        String sql = "SELECT * FROM borrowers WHERE id = ?";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Borrower(id, rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Borrower> findAll() {
        List<Borrower> list = new ArrayList<>();
        String sql = "SELECT * FROM borrowers ORDER BY id";
        try (Connection c = ConnectorDB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Borrower(rs.getString("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean insert(Borrower u) {
        String sql = "INSERT INTO borrowers(id, name) VALUES(?,?)";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getId());
            ps.setString(2, u.getName());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Borrower u) {
        String sql = "UPDATE borrowers SET name = ? WHERE id = ?";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM borrowers WHERE id = ?";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
