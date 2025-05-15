package uet.group1.librarymanagement.controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uet.group1.librarymanagement.Entities.Person;
import uet.group1.librarymanagement.Service.AuthService;
import uet.group1.librarymanagement.Service.BookService;
import uet.group1.librarymanagement.Service.UserService;
import uet.group1.librarymanagement.Service.BorrowService;

public class LoginScreen extends VBox {

    private final Stage primaryStage;
    private final AuthService authService;
    private final BookService bookService;
    private final UserService personService;
    private final BorrowService borrowService;

    private final TextField      txtUserId   = new TextField();
    private final PasswordField  txtPassword = new PasswordField();
    private final Label          lblMessage  = new Label();

    public LoginScreen(Stage primaryStage,
                       AuthService authService,
                       BookService bookService,
                       UserService personService,
                       BorrowService borrowService) {
        this.primaryStage  = primaryStage;
        this.authService   = authService;
        this.bookService   = bookService;
        this.personService = personService;
        this.borrowService = borrowService;

        setAlignment(Pos.CENTER);
        setSpacing(10);
        setPadding(new Insets(20));

        Label header = new Label("Library Management System");
        header.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");

        txtUserId.setPromptText("User ID");
        txtPassword.setPromptText("Password");
        lblMessage.setStyle("-fx-text-fill:red;");

        Button btnLogin    = new Button("Login");
        Button btnRegister = new Button("Register");
        btnLogin.setOnAction(e -> handleLogin());
        btnRegister.setOnAction(e -> lblMessage.setText("Please contact admin to register."));

        HBox buttonBox = new HBox(10, btnLogin, btnRegister);
        buttonBox.setAlignment(Pos.CENTER);

        getChildren().addAll(
                header,
                txtUserId,
                txtPassword,
                buttonBox,
                lblMessage
        );
    }

    private void handleLogin() {
        String id  = txtUserId.getText().trim();
        String pwd = txtPassword.getText().trim();

        java.util.Optional<Person> opt = authService.login(id, pwd);
        if (opt.isPresent()) {
            Person user = opt.get();
            Parent dashboard;
            if (user.getRole() == Person.Role.ADMIN) {
                dashboard = new AdminDashboard(
                        primaryStage,
                        authService,
                        bookService,
                        personService,
                        borrowService
                );
            } else {
                dashboard = new BorrowerDashboard(
                        primaryStage,
                        authService,
                        bookService,
                        personService,
                        borrowService
                );
            }
            primaryStage.getScene().setRoot(dashboard);
            primaryStage.setMaximized(true);
        } else {
            lblMessage.setText("Invalid ID or password");
        }
    }
}
