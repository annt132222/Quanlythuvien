package uet.group1.librarymanagement;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uet.group1.librarymanagement.controllers.LoginScreen;
import uet.group1.librarymanagement.Service.AuthService;
import uet.group1.librarymanagement.Service.BookService;
import uet.group1.librarymanagement.Service.UserService;
import uet.group1.librarymanagement.Service.BorrowService;

public class LibraryManagementMain extends Application {
    private final AuthService   authService   = new AuthService();
    private final BookService   bookService   = new BookService();
    private final UserService personService = new UserService();
    private final BorrowService borrowService = new BorrowService();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Library Management");

        LoginScreen login = new LoginScreen(
                primaryStage,
                authService,
                bookService,
                personService,
                borrowService
        );

        primaryStage.setScene(new Scene(login));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



