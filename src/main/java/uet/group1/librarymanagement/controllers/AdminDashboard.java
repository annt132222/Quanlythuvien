package uet.group1.librarymanagement.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import uet.group1.librarymanagement.Entities.Book;
import uet.group1.librarymanagement.Entities.Person;
import uet.group1.librarymanagement.Service.AuthService;
import uet.group1.librarymanagement.Service.BookService;
import uet.group1.librarymanagement.Service.BorrowService;
import uet.group1.librarymanagement.Service.UserService;

import java.util.List;
import java.util.Optional;

/**
 * Admin dashboard: quản lý sách và tài khoản.
 */
public final class AdminDashboard extends BorderPane {
    private final Stage        primaryStage;
    private final AuthService  authService;
    private final BookService  bookService;
    private final UserService personService;
    private final BorrowService borrowService;

    private final TableView<Book>   bookTable  = new TableView<>();
    private final TableView<Person> userTable  = new TableView<>();
    private final TextField         txtSearchTitle  = new TextField();
    private final TextField         txtSearchAuthor = new TextField();

    public AdminDashboard(Stage stage,
                          AuthService authService,
                          BookService bookService,
                          UserService personService,
                          BorrowService borrowService) {
        this.primaryStage   = stage;
        this.authService    = authService;
        this.bookService    = bookService;
        this.personService  = personService;
        this.borrowService  = borrowService;

        // Header
        Label header = new Label("Admin Dashboard");
        header.setStyle("-fx-font-size:20px; -fx-font-weight:bold;");
        setTop(header);
        BorderPane.setAlignment(header, Pos.CENTER);
        BorderPane.setMargin(header, new Insets(10));

        // Tabs
        TabPane tabs = new TabPane();
        tabs.getTabs().addAll(
                new Tab("Books",    buildBooksPane()),
                new Tab("Users",    buildUsersPane())
        );
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        setCenter(tabs);
    }

    private Node buildBooksPane() {
        // 1) Search box
        txtSearchTitle.setPromptText("Title keyword");
        txtSearchAuthor.setPromptText("Author keyword");
        Button btnSearchTitle  = new Button("Search Title");
        Button btnSearchAuthor = new Button("Search Author");
        Button btnRefreshBooks = new Button("Refresh");
        btnSearchTitle .setOnAction(e -> onSearchTitle());
        btnSearchAuthor.setOnAction(e -> onSearchAuthor());
        btnRefreshBooks.setOnAction(e -> reloadBooks());
        HBox searchBox = new HBox(5,
                new Label("Title:"), txtSearchTitle, btnSearchTitle,
                new Label("Author:"), txtSearchAuthor, btnSearchAuthor,
                btnRefreshBooks
        );
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(5));

        // 2) Book table
        bookTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Book,Integer> colId     = new TableColumn<>("ID");
        TableColumn<Book,String>  colTitle  = new TableColumn<>("Title");
        TableColumn<Book,String>  colAuthor = new TableColumn<>("Author");
        colId    .setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle .setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookTable.getColumns().setAll(colId, colTitle, colAuthor);

        reloadBooks();

        // 3) CRUD buttons
        Button btnAdd    = new Button("Add");
        Button btnUpdate = new Button("Update");
        Button btnDelete = new Button("Delete");
        HBox btnBox = new HBox(10, btnAdd, btnUpdate, btnDelete);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(5));

        btnAdd   .setOnAction(e -> onAddBook());
        btnUpdate.setOnAction(e -> onUpdateBook());
        btnDelete.setOnAction(e -> onDeleteBook());

        // 4) Compose
        VBox v = new VBox(10, searchBox, bookTable, btnBox);
        v.setPadding(new Insets(10));
        return v;
    }

    private Node buildUsersPane() {
        // 1) User table
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Person,String> colUid   = new TableColumn<>("ID");
        TableColumn<Person,String> colUname = new TableColumn<>("Name");
        TableColumn<Person,String> colRole  = new TableColumn<>("Role");
        colUid  .setCellValueFactory(new PropertyValueFactory<>("id"));
        colUname.setCellValueFactory(new PropertyValueFactory<>("name"));
        colRole .setCellValueFactory(new PropertyValueFactory<>("role"));
        userTable.getColumns().setAll(colUid, colUname, colRole);
        reloadUsers();

        // 2) CRUD buttons
        Button btnAddUser    = new Button("Add");
        Button btnUpdateUser = new Button("Update");
        Button btnDeleteUser = new Button("Delete");
        Button btnRefreshUsr = new Button("Refresh");
        HBox btnBox = new HBox(10, btnAddUser, btnUpdateUser, btnDeleteUser, btnRefreshUsr);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(5));

        btnAddUser   .setOnAction(e -> onAddUser());
        btnUpdateUser.setOnAction(e -> onUpdateUser());
        btnDeleteUser.setOnAction(e -> onDeleteUser());
        btnRefreshUsr.setOnAction(e -> reloadUsers());

        // 3) Compose
        VBox v = new VBox(10, userTable, btnBox);
        v.setPadding(new Insets(10));
        return v;
    }

    // === Book handlers ===

    private void reloadBooks() {
        List<Book> list = bookService.findAllBooks();
        ObservableList<Book> data = FXCollections.observableArrayList(list);
        bookTable.setItems(data);
    }

    private void onSearchTitle() {
        String kw = txtSearchTitle.getText().trim();
        List<Book> list = bookService.searchByTitle(kw);
        bookTable.setItems(FXCollections.observableArrayList(list));
    }

    private void onSearchAuthor() {
        String kw = txtSearchAuthor.getText().trim();
        List<Book> list = bookService.searchByAuthor(kw);
        bookTable.setItems(FXCollections.observableArrayList(list));
    }

    private void onAddBook() {
        // ví dụ: dùng TextInputDialog để hỏi Title; tương tự Author, ISBN...
        TextInputDialog dlg = new TextInputDialog();
        dlg.setHeaderText("Add new book");
        dlg.setContentText("Title:");
        dlg.showAndWait().ifPresent(title -> {
            // thực ra nên có form đầy đủ, đây là ví dụ đơn giản
            Book b = new Book(0, "", "", "", "", "", "", "", "", 0, null, 0, title, 0);
            bookService.addBook(b);
            reloadBooks();
        });
    }

    private void onUpdateBook() {
        Book sel = bookTable.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Select a book first."); return; }
        TextInputDialog dlg = new TextInputDialog(sel.getTitle());
        dlg.setHeaderText("Update book title");
        dlg.setContentText("New Title:");
        dlg.showAndWait().ifPresent(newTitle -> {
            sel.setTitle(newTitle);
            bookService.updateBook(sel);
            reloadBooks();
        });
    }

    private void onDeleteBook() {
        Book sel = bookTable.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Select a book first."); return; }
        bookService.deleteBook(sel.getId());
        reloadBooks();
    }

    // === User handlers ===

    private void reloadUsers() {
        List<Person> list = personService.findAllUsers();
        userTable.setItems(FXCollections.observableArrayList(list));
    }

    private void onAddUser() {
        Dialog<Person> dlg = new Dialog<>();
        dlg.setHeaderText("Add new user");
        // TODO: xây form nhập id, name, password, role; rồi dlg.showAndWait()
        // giả sử userService.register(...) rồi reloadUsers()
    }

    private void onUpdateUser() {
        Person sel = userTable.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Select a user first."); return; }
        TextInputDialog dlg = new TextInputDialog(sel.getName());
        dlg.setHeaderText("Update user name");
        dlg.setContentText("New Name:");
        dlg.showAndWait().ifPresent(newName -> {
            sel.setName(newName);
            personService.updateUser(sel);
            reloadUsers();
        });
    }

    private void onDeleteUser() {
        Person sel = userTable.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Select a user first."); return; }
        personService.deleteUser(sel.getId());
        reloadUsers();
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }

    // === Logout ===

    private void onLogout() {
        authService.logout();
        Parent login = new LoginScreen(
                primaryStage,
                authService,
                bookService,
                personService,
                borrowService
        );
        primaryStage.getScene().setRoot(login);
    }
}


//package uet.group1.librarymanagement.controllers;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Parent;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//import uet.group1.librarymanagement.Entities.Book;
//import uet.group1.librarymanagement.Entities.Person;
//import uet.group1.librarymanagement.Service.AuthService;
//import uet.group1.librarymanagement.Service.BookService;
//import uet.group1.librarymanagement.Service.BorrowService;
//import uet.group1.librarymanagement.Service.UserService;
//
///**
// * Admin dashboard view (Java-only, no FXML).
// * Shows a table of books and allows adding books,
// * deleting user accounts (cascade deletes borrow history),
// * and logging out.
// */
//public final class AdminDashboard extends VBox {
//
//    private final Stage primaryStage;
//    private final AuthService authService;
//    private final BookService bookService;
//    private final UserService personService;
//    private final BorrowService borrowService;
//    private final TableView<Book> bookTable;
//
//    public AdminDashboard(Stage primaryStage,
//                          AuthService authService,
//                          BookService bookService,
//                          UserService personService,
//                          BorrowService borrowService) {
//        this.primaryStage   = primaryStage;
//        this.authService    = authService;
//        this.bookService    = bookService;
//        this.personService  = personService;
//        this.borrowService  = borrowService;
//
//        setSpacing(10);
//        setPadding(new Insets(10));
//
//        Label headerLabel = new Label("Admin Dashboard");
//        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
//
//        bookTable = createBookTable();
//        reloadBooks();
//
//        HBox buttonBox = createButtonBox();
//
//        getChildren().addAll(headerLabel, bookTable, buttonBox);
//    }
//
//    private TableView<Book> createBookTable() {
//        TableView<Book> table = new TableView<>();
//        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//
//        TableColumn<Book, Integer> idColumn = new TableColumn<>("ID");
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//
//        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
//        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
//
//        TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
//        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
//
//        table.getColumns().addAll(idColumn, titleColumn, authorColumn);
//        return table;
//    }
//
//    private HBox createButtonBox() {
//        Button addBookButton = new Button("Add Book");
//        addBookButton.setOnAction(e -> onAddBook());
//
//        Button deleteAccountButton = new Button("Delete Account");
//        deleteAccountButton.setOnAction(e -> onDeleteAccount());
//
//        Button logoutButton = new Button("Logout");
//        logoutButton.setOnAction(e -> onLogout());
//
//        HBox box = new HBox(10, addBookButton, deleteAccountButton, logoutButton);
//        box.setAlignment(Pos.CENTER);
//        return box;
//    }
//
//    private void reloadBooks() {
//        ObservableList<Book> books = FXCollections
//                .observableArrayList(bookService.findAllBooks());
//        bookTable.setItems(books);
//    }
//
//    private void onAddBook() {
//        // TODO: show an input dialog or new window to collect book details,
//        // then call bookService.addBook(...)
//        // For now we simply reload the table
//        reloadBooks();
//    }
//
//    private void onDeleteAccount() {
//        TextInputDialog dialog = new TextInputDialog();
//        dialog.setTitle("Delete User Account");
//        dialog.setHeaderText("Delete User Account");
//        dialog.setContentText("Enter Person ID:");
//        dialog.showAndWait().ifPresent(id -> {
//            boolean success = personService.deleteUser(id);
//            Alert alert = new Alert(success
//                    ? Alert.AlertType.INFORMATION
//                    : Alert.AlertType.ERROR);
//            alert.setHeaderText(null);
//            alert.setContentText(success
//                    ? "Account deleted."
//                    : "Cannot delete account (active loans or not found).");
//            alert.showAndWait();
//        });
//    }
//
//    private void onLogout() {
//        authService.logout();
//        Parent loginScreen = new LoginScreen(
//                primaryStage,
//                authService,
//                bookService,
//                personService,
//                borrowService
//        );
//        primaryStage.getScene().setRoot(loginScreen);
//    }
//}
