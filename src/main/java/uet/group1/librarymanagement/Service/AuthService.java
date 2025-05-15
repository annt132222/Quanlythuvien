package uet.group1.librarymanagement.Service;

import uet.group1.librarymanagement.dao.UserDao;
import uet.group1.librarymanagement.dao.UserDaoImpl;
import uet.group1.librarymanagement.Entities.Person;

import java.util.Optional;

public class AuthService {
    private final UserDao userDao = new UserDaoImpl();
    private Person currentUser;

    /**
     * Đăng nhập với ID và mật khẩu.
     * Nếu thành công, lưu currentUser và trả về Optional.of(user),
     * nếu thất bại trả về Optional.empty().
     */
    public Optional<Person> login(String id, String password) {
        Optional<Person> opt = userDao.findById(id);
        if (opt.isPresent() && opt.get().getPassword().equals(password)) {
            currentUser = opt.get();
            return opt;
        }
        return Optional.empty();
    }

    /**
     * Đăng ký tài khoản mới.
     * @return true nếu thành công; false nếu ID đã tồn tại hoặc lỗi DB.
     */
    public boolean register(Person user) {
        // userDao.insert trả về false nếu duplicate key
        return userDao.insert(user);
    }

    /**
     * Trả về người dùng vừa đăng nhập, hoặc null nếu chưa login.
     */
    public Person getCurrentUser() {
        return currentUser;
    }

    /**
     * Đăng xuất, xóa session hiện tại.
     */
    public void logout() {
        currentUser = null;
    }
}
