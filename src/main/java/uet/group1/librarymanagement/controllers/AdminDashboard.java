package uet.group1.librarymanagement.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import uet.group1.librarymanagement.Entities.Admin;
import uet.group1.librarymanagement.Entities.Book;
import uet.group1.librarymanagement.Entities.Borrower;
import uet.group1.librarymanagement.Entities.Person;
import uet.group1.librarymanagement.Service.AuthService;
import uet.group1.librarymanagement.Service.BookService;
import uet.group1.librarymanagement.Service.BorrowService;
import uet.group1.librarymanagement.Service.UserService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Admin dashboard view (pure JavaFX, no FXML).
 * Tabs for Books (with ISBN search) and Users, plus Logout button.
 */
public final class AdminDashboard extends BorderPane {

    private final Stage        primaryStage;
    private final AuthService  authService;
    private final BookService  bookService;
    private final UserService  userService;
    private final BorrowService borrowService;

    private final TableView<Book>   bookTable = new TableView<Book>();
    private final TableView<Person> userTable = new TableView<Person>();

    public AdminDashboard(Stage primaryStage,
                          AuthService authService,
                          BookService bookService,
                          UserService userService,
                          BorrowService borrowService) {
        this.primaryStage  = primaryStage;
        this.authService   = authService;
        this.bookService   = bookService;
        this.userService   = userService;
        this.borrowService = borrowService;

        setPadding(new Insets(10));

        // Header
        Label header = new Label("Admin Dashboard");
        header.setStyle("-fx-font-size:20px; -fx-font-weight:bold;");
        setTop(header);
        BorderPane.setAlignment(header, Pos.CENTER);

        // Tabs
        TabPane tabs = new TabPane(
                new Tab("Books", buildBooksPane()),
                new Tab("Users", buildUsersPane())
        );
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        setCenter(tabs);

        // Logout at bottom-right
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> onLogout());
        HBox logoutBox = new HBox(btnLogout);
        logoutBox.setAlignment(Pos.BOTTOM_RIGHT);
        logoutBox.setPadding(new Insets(5));
        setBottom(logoutBox);
    }

    private Parent buildBooksPane() {
        // ISBN search bar
        TextField isbnSearchField = new TextField();
        isbnSearchField.setPromptText("Search by ISBN");
        Button btnSearchIsbn = new Button("Search");
        btnSearchIsbn.setOnAction(e -> {
            String isbn = isbnSearchField.getText().trim();
            if (isbn.isEmpty()) {
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setHeaderText(null);
                warning.setContentText("Please enter IBSN code.");
                warning.showAndWait();
                return;
            }
            // nếu có nhập, thì thực hiện tìm
            Optional<Book> ob = bookService.findByIsbn(isbn);
            ObservableList<Book> list = FXCollections.observableArrayList();
            ob.ifPresent(list::add);
            bookTable.setItems(list);
        });
        Button btnRefreshBooks = new Button("Refresh");
        btnRefreshBooks.setOnAction(e -> reloadBooks());
        HBox searchBar = new HBox(5, isbnSearchField, btnSearchIsbn, btnRefreshBooks);
        searchBar.setPadding(new Insets(5));
        searchBar.setAlignment(Pos.CENTER_LEFT);

        // Table columns
        bookTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Book, String> coverCol = new TableColumn<Book, String>("Cover");
        coverCol.setCellValueFactory(new PropertyValueFactory<Book, String>("imgUrl"));
        coverCol.setCellFactory(col -> new TableCell<Book,String>() {
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

        TableColumn<Book, String> isbnCol   = new TableColumn<Book,String>("ISBN");
        TableColumn<Book, String> titleCol  = new TableColumn<Book,String>("Title");
        TableColumn<Book, String> authorCol = new TableColumn<Book,String>("Author");
        TableColumn<Book, String> genreCol  = new TableColumn<Book,String>("Genre");

        isbnCol  .setCellValueFactory(new PropertyValueFactory<Book, String>("isbn"));
        titleCol .setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<Book, String>("author"));
        genreCol .setCellValueFactory(new PropertyValueFactory<Book, String>("genre"));

        for (TableColumn<?,?> col : java.util.Arrays.asList(
                coverCol, isbnCol, titleCol, authorCol, genreCol)) {
            col.setSortable(false);
        }
        bookTable.getColumns().setAll(
                coverCol, isbnCol, titleCol, authorCol, genreCol
        );
        VBox.setVgrow(bookTable, Priority.ALWAYS);
        reloadBooks();

        // CRUD buttons
        Button btnAdd    = new Button("Add Book");
        Button btnUpdate = new Button("Update Book");
        Button btnDelete = new Button("Delete Book");
        btnAdd   .setOnAction(e -> onAddBook());
        btnUpdate.setOnAction(e -> onUpdateBook());
        btnDelete.setOnAction(e -> onDeleteBook());
        HBox btnBox = new HBox(10, btnAdd, btnUpdate, btnDelete);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(5));

        return new VBox(10, searchBar, bookTable, btnBox);
    }

    private Parent buildUsersPane() {
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Person, String> idCol   = new TableColumn<Person,String>("ID");
        TableColumn<Person, String> nameCol = new TableColumn<Person,String>("Name");
        TableColumn<Person, String> roleCol = new TableColumn<Person,String>("Role");

        idCol  .setCellValueFactory(new PropertyValueFactory<Person, String>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("name"));
        roleCol.setCellValueFactory(new PropertyValueFactory<Person, String>("role"));

        for (TableColumn<?,?> col : java.util.Arrays.asList(idCol, nameCol, roleCol)) {
            col.setSortable(false);
        }
        userTable.getColumns().setAll(idCol, nameCol, roleCol);
        VBox.setVgrow(userTable, Priority.ALWAYS);
        reloadUsers();

        Button btnAddUser    = new Button("Add User");
        Button btnUpdateUser = new Button("Update User");
        Button btnDeleteUser = new Button("Delete User");
        btnAddUser   .setOnAction(e -> onAddUser());
        btnUpdateUser.setOnAction(e -> onUpdateUser());
        btnDeleteUser.setOnAction(e -> onDeleteUser());
        HBox btnBox = new HBox(10, btnAddUser, btnUpdateUser, btnDeleteUser);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(5));

        return new VBox(10, userTable, btnBox);
    }

    private void reloadBooks() {
        ObservableList<Book> data =
                FXCollections.observableArrayList(bookService.findAllBooks());
        bookTable.setItems(data);
    }

    private void reloadUsers() {
        ObservableList<Person> data =
                FXCollections.observableArrayList(userService.findAllUsers());
        userTable.setItems(data);
    }

    private void onAddBook() {
        Dialog<Book> dialog = new Dialog<Book>();
        dialog.setTitle("Add New Book");
        dialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);

        // Các field nhập liệu
        TextField authorField       = new TextField();
        TextField titleField        = new TextField();
        TextField genreField        = new TextField();
        TextField imgUrlField       = new TextField();
        TextField isbnField         = new TextField();
        TextField isbn13Field       = new TextField();
        TextField formatField       = new TextField();
        TextField descField         = new TextField();
        TextField linkField         = new TextField();
        TextField pagesField        = new TextField();
        TextField ratingField       = new TextField();
        TextField reviewsField      = new TextField();
        TextField totalRatingsField = new TextField();

        // Layout grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(new Label("Author:"),         0, 0); grid.add(authorField,       1, 0);
        grid.add(new Label("Title:"),          0, 1); grid.add(titleField,        1, 1);
        grid.add(new Label("Genre:"),          0, 2); grid.add(genreField,        1, 2);
        grid.add(new Label("Cover URL:"),      0, 3); grid.add(imgUrlField,       1, 3);
        grid.add(new Label("ISBN:"),           0, 4); grid.add(isbnField,         1, 4);
        grid.add(new Label("ISBN13:"),         0, 5); grid.add(isbn13Field,       1, 5);
        grid.add(new Label("Format:"),         0, 6); grid.add(formatField,       1, 6);
        grid.add(new Label("Description:"),    0, 7); grid.add(descField,         1, 7);
        grid.add(new Label("Link:"),           0, 8); grid.add(linkField,         1, 8);
        grid.add(new Label("Pages:"),          0, 9); grid.add(pagesField,        1, 9);
        grid.add(new Label("Rating (0.00):"),  0,10); grid.add(ratingField,       1,10);
        grid.add(new Label("Reviews:"),        0,11); grid.add(reviewsField,      1,11);
        grid.add(new Label("TotalRatings:"),   0,12); grid.add(totalRatingsField, 1,12);

        dialog.getDialogPane().setContent(grid);

        // Lấy nút OK và bind disable nếu bất kỳ field nào trống
        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(
                authorField.textProperty().isEmpty()
                        .or(titleField.textProperty().isEmpty())
                        .or(genreField.textProperty().isEmpty())
                        .or(imgUrlField.textProperty().isEmpty())
                        .or(isbnField.textProperty().isEmpty())
                        .or(isbn13Field.textProperty().isEmpty())
                        .or(formatField.textProperty().isEmpty())
                        .or(descField.textProperty().isEmpty())
                        .or(linkField.textProperty().isEmpty())
                        .or(pagesField.textProperty().isEmpty())
                        .or(ratingField.textProperty().isEmpty())
                        .or(reviewsField.textProperty().isEmpty())
                        .or(totalRatingsField.textProperty().isEmpty())
        );

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    return new Book(
                            0,
                            authorField.getText().trim(),
                            formatField.getText().trim(),
                            descField.getText().trim(),
                            genreField.getText().trim(),
                            imgUrlField.getText().trim(),
                            isbnField.getText().trim(),
                            isbn13Field.getText().trim(),
                            linkField.getText().trim(),
                            Integer.parseInt(pagesField.getText().trim()),
                            new java.math.BigDecimal(ratingField.getText().trim()),
                            Integer.parseInt(reviewsField.getText().trim()),
                            titleField.getText().trim(),
                            Integer.parseInt(totalRatingsField.getText().trim())
                    );
                } catch (Exception ex) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(book -> {
            boolean success = bookService.addBook(book);
            Alert alert = new Alert(success
                    ? Alert.AlertType.INFORMATION
                    : Alert.AlertType.ERROR
            );
            alert.setHeaderText(null);
            alert.setContentText(success
                    ? "Book added successfully."
                    : "Failed to add book."
            );
            alert.showAndWait();
            if (success) {
                reloadBooks();
            }
        });
    }

    private void onUpdateBook() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Select a book first.").showAndWait();
            return;
        }
        Dialog<Book> dialog = new Dialog<Book>();
        dialog.setTitle("Update Book");
        dialog.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField authorField       = new TextField(selected.getAuthor());
        TextField titleField        = new TextField(selected.getTitle());
        TextField genreField        = new TextField(selected.getGenre());
        TextField imgUrlField       = new TextField(selected.getImgUrl());
        TextField isbnField         = new TextField(selected.getIsbn());
        TextField isbn13Field       = new TextField(selected.getIsbn13());
        TextField formatField       = new TextField(selected.getBookFormat());
        TextField descField         = new TextField(selected.getDescription());
        TextField linkField         = new TextField(selected.getLink());
        TextField pagesField        = new TextField(String.valueOf(selected.getPages()));
        TextField ratingField       = new TextField(selected.getRating().toString());
        TextField reviewsField      = new TextField(String.valueOf(selected.getReviews()));
        TextField totalRatingsField = new TextField(String.valueOf(selected.getTotalRatings()));

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(new Label("Author:"),        0,0); grid.add(authorField,      1,0);
        grid.add(new Label("Title:"),         0,1); grid.add(titleField,       1,1);
        grid.add(new Label("Genre:"),         0,2); grid.add(genreField,       1,2);
        grid.add(new Label("Cover URL:"),     0,3); grid.add(imgUrlField,      1,3);
        grid.add(new Label("ISBN:"),          0,4); grid.add(isbnField,        1,4);
        grid.add(new Label("ISBN13:"),        0,5); grid.add(isbn13Field,      1,5);
        grid.add(new Label("Format:"),        0,6); grid.add(formatField,      1,6);
        grid.add(new Label("Description:"),   0,7); grid.add(descField,        1,7);
        grid.add(new Label("Link:"),          0,8); grid.add(linkField,        1,8);
        grid.add(new Label("Pages:"),         0,9); grid.add(pagesField,       1,9);
        grid.add(new Label("Rating:"),        0,10);grid.add(ratingField,      1,10);
        grid.add(new Label("Reviews:"),       0,11);grid.add(reviewsField,     1,11);
        grid.add(new Label("TotalRatings:"),  0,12);grid.add(totalRatingsField,1,12);

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(
                titleField.textProperty().isEmpty()
                        .or(authorField.textProperty().isEmpty())
        );

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    return new Book(
                            selected.getId(),
                            authorField.getText().trim(),
                            formatField.getText().trim(),
                            descField.getText().trim(),
                            genreField.getText().trim(),
                            imgUrlField.getText().trim(),
                            isbnField.getText().trim(),
                            isbn13Field.getText().trim(),
                            linkField.getText().trim(),
                            Integer.parseInt(pagesField.getText().trim()),
                            new BigDecimal(ratingField.getText().trim()),
                            Integer.parseInt(reviewsField.getText().trim()),
                            titleField.getText().trim(),
                            Integer.parseInt(totalRatingsField.getText().trim())
                    );
                } catch (Exception ex) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(book -> {
            boolean success = bookService.updateBook(book);
            Alert alert = new Alert(success
                    ? Alert.AlertType.INFORMATION
                    : Alert.AlertType.ERROR
            );
            alert.setHeaderText(null);
            alert.setContentText(success
                    ? "Book updated."
                    : "Failed to update book."
            );
            alert.showAndWait();
            if (success) reloadBooks();
        });
    }

    private void onDeleteBook() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Select a book first.").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText(null);
        confirm.setContentText("Delete \"" + selected.getTitle() + "\"?");
        confirm.showAndWait().filter(ButtonType.OK::equals).ifPresent(b -> {
            boolean success = bookService.deleteBook(selected.getId());
            Alert alert = new Alert(success
                    ? Alert.AlertType.INFORMATION
                    : Alert.AlertType.ERROR
            );
            alert.setHeaderText(null);
            alert.setContentText(success
                    ? "Book deleted."
                    : "Failed to delete book."
            );
            alert.showAndWait();
            if (success) reloadBooks();
        });
    }

    private void onAddUser() {
        Dialog<Person> dlg = new Dialog<Person>();
        dlg.setTitle("Add User");
        dlg.getDialogPane().getButtonTypes()
                .addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField idFld      = new TextField();
        TextField nameFld    = new TextField();
        PasswordField pwdFld = new PasswordField();
        ComboBox<Person.Role> roleBox = new ComboBox<Person.Role>();
        roleBox.getItems().addAll(Person.Role.BORROWER, Person.Role.ADMIN);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.add(new Label("ID:"),     0,0); grid.add(idFld,   1,0);
        grid.add(new Label("Name:"),   0,1); grid.add(nameFld, 1,1);
        grid.add(new Label("Password:"),0,2); grid.add(pwdFld,  1,2);
        grid.add(new Label("Role:"),   0,3); grid.add(roleBox, 1,3);

        Node ok = dlg.getDialogPane().lookupButton(ButtonType.OK);
        ok.disableProperty().bind(
                idFld.textProperty().isEmpty()
                        .or(nameFld.textProperty().isEmpty())
                        .or(pwdFld.textProperty().isEmpty())
                        .or(roleBox.valueProperty().isNull())
        );

        dlg.getDialogPane().setContent(grid);
        dlg.setResultConverter(b -> {
            if (b == ButtonType.OK) {
                if (roleBox.getValue() == Person.Role.ADMIN) {
                    return new Admin(idFld.getText(), nameFld.getText(), pwdFld.getText());
                } else {
                    return new Borrower(idFld.getText(), nameFld.getText(), pwdFld.getText());
                }
            }
            return null;
        });

        dlg.showAndWait().ifPresent(user -> {
            boolean success = userService.addUser(user);
            Alert a = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            a.setHeaderText(null);
            a.setContentText(success ? "User added." : "Failed: ID exists.");
            a.showAndWait();
            if (success) reloadUsers();
        });
    }

    private void onUpdateUser() {
        Person sel = userTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Select a user first.").showAndWait();
            return;
        }
        TextInputDialog dlg = new TextInputDialog(sel.getName());
        dlg.setHeaderText("Update Name");
        dlg.setContentText("New name:");
        dlg.showAndWait().ifPresent(newName -> {
            sel.setName(newName);
            boolean success = userService.updateUser(sel);
            Alert a = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            a.setHeaderText(null);
            a.setContentText(success ? "User updated." : "Failed to update.");
            a.showAndWait();
            if (success) reloadUsers();
        });
    }

    private void onDeleteUser() {
        Person sel = userTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Select a user first.").showAndWait();
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText(null);
        confirm.setContentText("Delete user \"" + sel.getName() + "\"?");
        confirm.showAndWait().filter(ButtonType.OK::equals).ifPresent(b -> {
            boolean success = userService.deleteUser(sel.getId());
            Alert a = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            a.setHeaderText(null);
            a.setContentText(success ? "User deleted." : "Failed to delete.");
            a.showAndWait();
            if (success) reloadUsers();
        });
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

