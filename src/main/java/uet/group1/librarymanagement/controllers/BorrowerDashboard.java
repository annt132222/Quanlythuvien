package uet.group1.librarymanagement.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uet.group1.librarymanagement.Entities.Book;
import uet.group1.librarymanagement.Entities.BorrowRecord;
import uet.group1.librarymanagement.Entities.Person;
import uet.group1.librarymanagement.Service.AuthService;
import uet.group1.librarymanagement.Service.BookService;
import uet.group1.librarymanagement.Service.BorrowService;
import uet.group1.librarymanagement.Service.UserService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Borrower dashboard: xem catalog, mượn/ trả sách và xem current loans.
 */
public final class BorrowerDashboard extends TabPane {

    private final Stage primaryStage;
    private final AuthService   authService;
    private final BookService   bookService;
    private final BorrowService borrowService;
    private final UserService personService;
    private final Person currentUser;

    // Controls for catalog tab
    private final TableView<Book> bookTable = new TableView<>();
    private final TextField       txtSearch  = new TextField();
    private final TextField       txtIsbn    = new TextField();
    private final Label           lblCatalog = new Label();

    // Controls for loans tab
    private final TableView<BorrowRecord> loanTable = new TableView<>();
    private final Label                 lblLoans   = new Label();

    public BorrowerDashboard(Stage stage,
                             AuthService authService,
                             BookService bookService,
                             UserService personService,
                             BorrowService borrowService) {
        this.primaryStage  = stage;
        this.authService   = authService;
        this.bookService   = bookService;
        this.personService = personService;
        this.borrowService = borrowService;
        this.currentUser   = authService.getCurrentUser();

        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        getTabs().addAll(
                new Tab("Catalog", buildCatalogPane()),
                new Tab("My Loans", buildLoansPane())
        );
    }

    private Parent buildCatalogPane() {
        txtSearch.setPromptText("Search title/author");
        Button btnSearch = new Button("Search");
        btnSearch.setOnAction(e -> onSearch());
        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> reloadCatalog());

        HBox hbSearch = new HBox(5, txtSearch, btnSearch, btnRefresh);
        hbSearch.setAlignment(Pos.CENTER_LEFT);

        bookTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Book,String> colIsbn   = new TableColumn<>("ISBN");
        TableColumn<Book,String> colTitle  = new TableColumn<>("Title");
        TableColumn<Book,String> colAuthor = new TableColumn<>("Author");
        colIsbn  .setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitle .setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookTable.getColumns().setAll(colIsbn, colTitle, colAuthor);

        reloadCatalog();

        txtIsbn.setPromptText("ISBN to borrow");
        Button btnBorrow = new Button("Borrow");
        btnBorrow.setOnAction(e -> onBorrow());

        HBox hbBorrow = new HBox(5, txtIsbn, btnBorrow);
        hbBorrow.setAlignment(Pos.CENTER_LEFT);

        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> onLogout());

        VBox v = new VBox(10,
                hbSearch, bookTable, hbBorrow, lblCatalog, btnLogout
        );
        v.setPadding(new Insets(10));
        return v;
    }

    private Parent buildLoansPane() {
        loanTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<BorrowRecord,Integer> colBookId = new TableColumn<>("Book ID");
        TableColumn<BorrowRecord,String>  colDate   = new TableColumn<>("Borrowed At");
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colDate  .setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getBorrowDate().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        )
                )
        );
        loanTable.getColumns().setAll(colBookId, colDate);

        reloadLoans();

        Button btnReturn = new Button("Return Selected");
        btnReturn.setOnAction(e -> onReturn());
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> onLogout());

        HBox hb = new HBox(10, btnReturn, btnLogout);
        hb.setAlignment(Pos.CENTER);

        VBox v = new VBox(10, loanTable, lblLoans, hb);
        v.setPadding(new Insets(10));
        return v;
    }

    // === Catalog actions ===

    private void reloadCatalog() {
        List<Book> list = bookService.findAllBooks();
        bookTable.setItems(FXCollections.observableArrayList(list));
        lblCatalog.setText("");
    }

    private void onSearch() {
        String kw = txtSearch.getText().trim();
        List<Book> byTitle = bookService.searchByTitle(kw);
        List<Book> byAuthor = bookService.searchByAuthor(kw);
        ObservableList<Book> results = FXCollections.observableArrayList();
        results.addAll(byTitle);
        for (Book b : byAuthor) if (!results.contains(b)) results.add(b);
        bookTable.setItems(results);
    }

    private void onBorrow() {
        String isbn = txtIsbn.getText().trim();
        boolean ok = borrowService.borrowByIsbn(currentUser.getId(), isbn);
        lblCatalog.setText(ok ? "Borrowed!" : "Cannot borrow");
        reloadLoans();  // cập nhật luôn loans tab
    }

    // === Loans actions ===

    private void reloadLoans() {
        List<BorrowRecord> recs =
                borrowService.listCurrentLoans(currentUser.getId());
        loanTable.setItems(FXCollections.observableArrayList(recs));
        lblLoans.setText("");
    }

    private void onReturn() {
        BorrowRecord sel = loanTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            lblLoans.setText("Select a loan first.");
            return;
        }
        boolean ok = borrowService.returnByIsbn(
                currentUser.getId(),
                bookService.findById(sel.getBookId())
                        .map(Book::getIsbn)
                        .orElse("")
        );
        lblLoans.setText(ok ? "Returned!" : "Cannot return");
        reloadLoans();
        reloadCatalog();  // cập nhật catalog
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
//import uet.group1.librarymanagement.Entities.BorrowRecord;
//import uet.group1.librarymanagement.Entities.Person;
//import uet.group1.librarymanagement.Service.AuthService;
//import uet.group1.librarymanagement.Service.BookService;
//import uet.group1.librarymanagement.Service.BorrowService;
//import uet.group1.librarymanagement.Service.UserService;
//
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Optional;
//
///**
// * Dashboard cho role Borrower — Java-only, không dùng FXML.
// * Hiển thị sách, mượn/trả theo ISBN, xem danh sách đang mượn, và logout.
// */
//public final class BorrowerDashboard extends VBox {
//
//    private final Stage        primaryStage;
//    private final AuthService  authService;
//    private final BookService  bookService;
//    private final UserService personService;
//    private final BorrowService borrowService;
//    private final Person       currentUser;
//
//    private final TableView<Book> bookTable;
//    private final TextField       isbnField;
//    private final Label           messageLabel;
//
//    public BorrowerDashboard(Stage primaryStage,
//                             AuthService authService,
//                             BookService bookService,
//                             UserService personService,
//                             BorrowService borrowService) {
//        this.primaryStage  = primaryStage;
//        this.authService   = authService;
//        this.bookService   = bookService;
//        this.personService = personService;
//        this.borrowService = borrowService;
//        this.currentUser   = authService.getCurrentUser();
//
//        setSpacing(10);
//        setPadding(new Insets(10));
//        setAlignment(Pos.CENTER);
//
//        Label header = new Label("Borrower Dashboard");
//        header.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");
//
//        bookTable = createBookTable();
//        reloadBooks();
//
//        isbnField    = new TextField();
//        isbnField.setPromptText("ISBN to borrow");
//
//        Button btnBorrow   = new Button("Borrow");
//        Button btnMyLoans  = new Button("My Loans");
//        Button btnLogout   = new Button("Logout");
//
//        btnBorrow.setOnAction(e -> onBorrow());
//        btnMyLoans.setOnAction(e -> onMyLoans());
//        btnLogout.setOnAction(e -> onLogout());
//
//        HBox controls = new HBox(10, isbnField, btnBorrow, btnMyLoans, btnLogout);
//        controls.setAlignment(Pos.CENTER);
//
//        messageLabel = new Label();
//        messageLabel.setWrapText(true);
//
//        getChildren().addAll(header, bookTable, controls, messageLabel);
//    }
//
//    private TableView<Book> createBookTable() {
//        TableView<Book> table = new TableView<>();
//        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//
//        TableColumn<Book, String> isbnCol   = new TableColumn<>("ISBN");
//        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
//
//        TableColumn<Book, String> titleCol  = new TableColumn<>("Title");
//        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
//
//        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
//        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
//
//        table.getColumns().addAll(isbnCol, titleCol, authorCol);
//        return table;
//    }
//
//    private void reloadBooks() {
//        List<Book> list = bookService.findAllBooks();
//        ObservableList<Book> data = FXCollections.observableArrayList(list);
//        bookTable.setItems(data);
//    }
//
//    private void onBorrow() {
//        String isbn = isbnField.getText().trim();
//        boolean ok = borrowService.borrowByIsbn(currentUser.getId(), isbn);
//        messageLabel.setText(ok
//                ? "Borrow successful."
//                : "Borrow failed (invalid ISBN or already borrowed).");
//    }
//
//    private void onMyLoans() {
//        List<BorrowRecord> recs = borrowService.listCurrentLoans(currentUser.getId());
//        if (recs.isEmpty()) {
//            messageLabel.setText("You have no current loans.");
//            return;
//        }
//        StringBuilder sb = new StringBuilder();
//        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        for (BorrowRecord r : recs) {
//            Optional<Book> ob = bookService.findById(r.getBookId());
//            if (ob.isPresent()) {
//                Book b = ob.get();
//                sb.append(String.format("%s by %s — borrowed at %s%n",
//                        b.getTitle(), b.getAuthor(), r.getBorrowDate().format(fmt)));
//            } else {
//                sb.append(String.format("Book ID %d — borrowed at %s%n",
//                        r.getBookId(), r.getBorrowDate().format(fmt)));
//            }
//        }
//        messageLabel.setText(sb.toString());
//    }
//
//    private void onLogout() {
//        authService.logout();
//        // Gọi đúng constructor của LoginScreen với 5 tham số
//        Parent login = new LoginScreen(
//                primaryStage,
//                authService,
//                bookService,
//                personService,
//                borrowService
//        );
//        primaryStage.getScene().setRoot(login);
//    }
//}
