package uet.group1.librarymanagement.Entities;

public class Admin extends Person {
    public Admin(String id, String name, String password) {
        super(id, name, password, Role.ADMIN);
    }
    @Override
    public String toString() {
        return String.format("[%s] %s (Admin)", getId(), getName());
    }
}
