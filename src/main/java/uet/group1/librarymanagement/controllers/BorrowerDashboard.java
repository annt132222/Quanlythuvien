package uet.group1.librarymanagement.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import uet.group1.librarymanagement.Entities.Book;
import uet.group1.librarymanagement.Entities.BorrowRecord;
import uet.group1.librarymanagement.Entities.Person;
import uet.group1.librarymanagement.Service.AuthService;
import uet.group1.librarymanagement.Service.BookService;
import uet.group1.librarymanagement.Service.UserService;
import uet.group1.librarymanagement.Service.BorrowService;

import javafx.beans.property.SimpleStringProperty;
import java.util.Arrays;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public final class BorrowerDashboard extends BorderPane {

    private final Stage         primaryStage;
    private final AuthService   authService;
    private final BookService   bookService;
    private final UserService   userService;
    private final BorrowService borrowService;
    private final Person        currentUser;

    private final TableView<Book>       bookTable = new TableView<>();
    private final TextField             txtSearch = new TextField();
    private final TextField             txtIsbn   = new TextField();
    private final TableView<BorrowRecord> loanTable = new TableView<>();

    public BorrowerDashboard(Stage primaryStage,
                             AuthService authService,
                             BookService bookService,
                             UserService userService,
                             BorrowService borrowService) {
        this.primaryStage  = primaryStage;
        this.authService   = authService;
        this.bookService   = bookService;
        this.userService   = userService;
        this.borrowService = borrowService;
        this.currentUser   = authService.getCurrentUser();

        // TabPane với 2 tab
        TabPane tabs = new TabPane(
                new Tab("Catalog", buildCatalogPane()),
                new Tab("My Loans", buildLoansPane())
        );
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        setCenter(tabs);

        // Logout bottom-right
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> onLogout());
        HBox logoutBox = new HBox(btnLogout);
        logoutBox.setAlignment(Pos.BOTTOM_RIGHT);
        logoutBox.setPadding(new Insets(5));
        setBottom(logoutBox);
    }

    private Parent buildCatalogPane() {
        txtSearch.setPromptText("Search title or author");
        Button btnSearch = new Button("Search");
        btnSearch.setOnAction(e -> onSearchCatalog());
        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> reloadCatalog());

        HBox searchBar = new HBox(5, txtSearch, btnSearch, btnRefresh);
        searchBar.setPadding(new Insets(5));
        searchBar.setAlignment(Pos.CENTER_LEFT);

        // Configure bookTable
        bookTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Cover column (ảnh bìa), dùng getImgUrl()
        TableColumn<Book, String> coverCol = new TableColumn<>("Cover");
        coverCol.setCellValueFactory(new PropertyValueFactory<>("imgUrl"));
        coverCol.setCellFactory(col -> new TableCell<Book, String>() {
            private final ImageView iv = new ImageView();
            {
                iv.setFitWidth(200);
                iv.setFitHeight(200);
                iv.setPreserveRatio(true);
            }
            @Override
            protected void updateItem(String url, boolean empty) {
                super.updateItem(url, empty);
                if (empty || url == null || url.isEmpty()) {
                    setGraphic(null);
                } else {
                    iv.setImage(new Image(url, true));
                    setGraphic(iv);
                }
            }
        });

        // ISBN / Title / Author / Genre
        TableColumn<Book, String> isbnCol   = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        TableColumn<Book, String> titleCol  = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> genreCol  = new TableColumn<>("Genre");
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));

        // Disable sorting on all columns
        for (TableColumn<?,?> col : Arrays.asList(coverCol, isbnCol, titleCol, authorCol, genreCol)) {
            col.setSortable(false);
        }

        bookTable.getColumns().setAll(coverCol, isbnCol, titleCol, authorCol, genreCol);
        reloadCatalog();

        // Borrow controls
        txtIsbn.setPromptText("ISBN to borrow");
        Button btnBorrow = new Button("Borrow");
        btnBorrow.setOnAction(e -> onBorrow());
        HBox borrowBar = new HBox(5, txtIsbn, btnBorrow);
        borrowBar.setPadding(new Insets(5));
        borrowBar.setAlignment(Pos.CENTER_LEFT);

        VBox vbox = new VBox(10, searchBar, bookTable, borrowBar);
        vbox.setPadding(new Insets(10));
        return vbox;
    }

    private Parent buildLoansPane() {
        // 1) Configure loanTable
        loanTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ISBN column
        TableColumn<BorrowRecord, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(cellData -> {
            int bookId = cellData.getValue().getBookId();
            return bookService.findById(bookId)
                    .map(Book::getIsbn)
                    .map(SimpleStringProperty::new)
                    .orElse(new SimpleStringProperty(""));
        });

        // Title column
        TableColumn<BorrowRecord, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cellData -> {
            int bookId = cellData.getValue().getBookId();
            return bookService.findById(bookId)
                    .map(Book::getTitle)
                    .map(SimpleStringProperty::new)
                    .orElse(new SimpleStringProperty(""));
        });

        // Author column
        TableColumn<BorrowRecord, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(cellData -> {
            int bookId = cellData.getValue().getBookId();
            return bookService.findById(bookId)
                    .map(Book::getAuthor)
                    .map(SimpleStringProperty::new)
                    .orElse(new SimpleStringProperty(""));
        });

        // Borrow date column
        TableColumn<BorrowRecord, String> dateCol = new TableColumn<>("Borrowed At");
        dateCol.setCellValueFactory(cellData -> {
            String formatted = cellData.getValue()
                    .getBorrowDate()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return new SimpleStringProperty(formatted);
        });

        // Disable sorting on these columns if desired
        for (TableColumn<?,?> col : Arrays.asList(isbnCol, titleCol, authorCol, dateCol)) {
            col.setSortable(false);
        }

        loanTable.getColumns().setAll(isbnCol, titleCol, authorCol, dateCol);

        // 2) Load data
        reloadLoans();

        // 3) Return button
        Button btnReturn = new Button("Return Selected");
        btnReturn.setOnAction(e -> onReturn());

        HBox loanBar = new HBox(5, btnReturn);
        loanBar.setPadding(new Insets(5));
        loanBar.setAlignment(Pos.CENTER_LEFT);

        // 4) Assemble
        VBox vbox = new VBox(10, loanTable, loanBar);
        vbox.setPadding(new Insets(10));
        return vbox;
    }

    private void reloadCatalog() {
        List<Book> list = bookService.findAllBooks();
        ObservableList<Book> data = FXCollections.observableArrayList(list);
        bookTable.setItems(data);
    }

    private void onSearchCatalog() {
        String kw = txtSearch.getText().trim();

        if (kw.isEmpty()) {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setHeaderText(null);
            warning.setContentText("Please enter keyword.");
            warning.showAndWait();
            txtSearch.requestFocus();
            return;
        }

        List<Book> byTitle  = bookService.searchByTitle(kw);
        List<Book> byAuthor = bookService.searchByAuthor(kw);
        ObservableList<Book> merged = FXCollections.observableArrayList(byTitle);
        for (Book b : byAuthor) {
            if (!merged.contains(b)) merged.add(b);
        }
        bookTable.setItems(merged);
    }

    private void onBorrow() {
        String isbn = txtIsbn.getText().trim();
        if (isbn.isEmpty()) {
            Alert w = new Alert(Alert.AlertType.WARNING, "Please enter an ISBN before borrowing.");
            w.showAndWait();
            txtIsbn.requestFocus();
            return;
        }

        boolean ok = borrowService.borrowByIsbn(currentUser.getId(), isbn);
        Alert alert = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(ok
                ? "Borrow successful."
                : "Borrow failed: invalid ISBN or already borrowed."
        );
        alert.showAndWait();

        if (ok) {
            reloadCatalog();
            reloadLoans();
        }

        txtIsbn.clear();
        txtIsbn.requestFocus();
    }

    private void reloadLoans() {
        List<BorrowRecord> recs = borrowService.listCurrentLoans(currentUser.getId());
        ObservableList<BorrowRecord> data = FXCollections.observableArrayList(recs);
        loanTable.setItems(data);
    }

    private void onReturn() {
        BorrowRecord sel = loanTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Select a loan first.").showAndWait();
            return;
        }
        Optional<Book> ob = bookService.findById(sel.getBookId());
        String isbn = ob.map(Book::getIsbn).orElse("");
        boolean ok = borrowService.returnByIsbn(currentUser.getId(), isbn);
        Alert a = new Alert(ok ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(ok ? "Return successful." : "Return failed.");
        a.showAndWait();
        if (ok) {
            reloadCatalog();
            reloadLoans();
        }
    }

    private void onLogout() {
        authService.logout();
        Parent login = new LoginScreen(
                primaryStage,
                authService,
                bookService,
                userService,
                borrowService
        );
        primaryStage.getScene().setRoot(login);
    }
}
