package uet.group1.librarymanagement.Entities;

import java.util.HashMap;
import java.util.Map;

public abstract class Person {
    private String id;
    private String name;
    protected Map<Integer,Integer> borrowed = new HashMap<>();

    public Person(String id, String name) {
        this.id   = id;
        this.name = name;
    }

    public String getId()   { return id; }
    public String getName() { return name; }

    public void borrow(int bookId) {
        borrowed.put(bookId, borrowed.getOrDefault(bookId, 0) + 1);
    }

    public boolean returnBook(int bookId) {
        Integer cnt = borrowed.get(bookId);
        if (cnt == null || cnt == 0) return false;
        if (cnt == 1) borrowed.remove(bookId);
        else          borrowed.put(bookId, cnt - 1);
        return true;
    }

    public String borrowedInfo() {
        if (borrowed.isEmpty()) return "  (no books)";
        StringBuilder sb = new StringBuilder();
        borrowed.forEach((bId,cnt) ->
                sb.append(String.format("  - %s ×%d%n", bId, cnt))
        );
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s%nBorrowed:%n%s",
                id, name, borrowedInfo());
    }
}
