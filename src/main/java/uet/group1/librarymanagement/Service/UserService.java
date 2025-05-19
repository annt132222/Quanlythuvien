package uet.group1.librarymanagement.Service;

import uet.group1.librarymanagement.dao.UserDao;
import uet.group1.librarymanagement.dao.UserDaoImpl;
import uet.group1.librarymanagement.Entities.Person;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao = new UserDaoImpl();

    /** Thêm user mới. @return true nếu insert thành công. */
    public boolean addUser(Person u) {
        return userDao.insert(u);
    }

    /** Cập nhật thông tin user. @return true nếu update thành công. */
    public boolean updateUser(Person u) {
        return userDao.update(u);
    }

    /** Xóa user theo ID. @return true nếu delete thành công. */
    public boolean deleteUser(String id) {
        return userDao.delete(id);
    }

    /** Lấy tất cả user. */
    public List<Person> findAllUsers() {
        return userDao.findAll();
    }

    /** Tìm user theo ID. */
    public Optional<Person> findById(String id) {
        return userDao.findById(id);
    }
}

