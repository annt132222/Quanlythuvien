package uet.group1.librarymanagement.Entities;

public class Borrower extends Person {

    public Borrower(String id, String name, String password) {
        super(id, name, password, Role.BORROWER);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(
                String.format("[%s] %s (Borrower)", getId(), getName())
        );
        sb.append("\nBorrowed books:\n");
        if (borrowed.isEmpty()) sb.append("  (none)\n");
        else borrowed.forEach((bid,qty) ->
                sb.append(String.format("  - Book ID %d ×%d\n", bid, qty))
        );
        return sb.toString();
    }
}
