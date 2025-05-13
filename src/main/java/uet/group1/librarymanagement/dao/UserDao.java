package uet.group1.librarymanagement.dao;

import uet.group1.librarymanagement.Entities.*;

import java.util.List;

public interface UserDao extends Dao<Person, String> {
    List<Person> findByRole(Person.Role role);
}
