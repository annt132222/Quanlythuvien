package uet.group1.librarymanagement.dao;

import uet.group1.librarymanagement.Entities.*;
import uet.group1.librarymanagement.Utils.ConnectorDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    @Override
    public Optional<Person> findById(String id) {
        String sql = "SELECT * FROM users WHERE id=?";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String pwd  = rs.getString("password");
                    Person.Role role = Person.Role.valueOf(rs.getString("role"));
                    if (role == Person.Role.ADMIN) return Optional.of(new Admin(id, name, pwd));
                    else                           return Optional.of(new Borrower(id, name, pwd));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Person> findAll() {
        List<Person> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY role, id";
        try (Connection c = ConnectorDB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String pwd  = rs.getString("password");
                Person.Role role = Person.Role.valueOf(rs.getString("role"));
                if (role == Person.Role.ADMIN) list.add(new Admin(id,name,pwd));
                else                           list.add(new Borrower(id,name,pwd));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean insert(Person u) {
        String sql = "INSERT INTO users(id,name,password,role) VALUES(?,?,?,?)";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getId());
            ps.setString(2, u.getName());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getRole().name());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean update(Person u) {
        String sql = "UPDATE users SET name=?, password=?, role=? WHERE id=?";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getRole().name());
            ps.setString(4, u.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(String id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public List<Person> findByRole(Person.Role role) {
        List<Person> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role=? ORDER BY id";
        try (Connection c = ConnectorDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    String pwd  = rs.getString("password");
                    if (role == Person.Role.ADMIN)
                        list.add(new Admin(id, name, pwd));
                    else
                        list.add(new Borrower(id, name, pwd));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
