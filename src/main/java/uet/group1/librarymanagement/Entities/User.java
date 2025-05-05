package uet.group1.librarymanagement.Entities;

import java.util.ArrayList;
import java.util.List;

public class User {
    private static int counter = 1;
    private final int id;
    private String name;
    private final List<Integer> borrowedDocIds = new ArrayList<>();

    public User(String name) {
        this.id = counter++;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Integer> getBorrowedDocIds() { return borrowedDocIds; }

    @Override
    public String toString() {
        return String.format("[%d] %s – Đang mượn: %d",
                id, name, borrowedDocIds.size());
    }
}
