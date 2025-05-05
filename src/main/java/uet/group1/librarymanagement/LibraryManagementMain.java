package uet.group1.librarymanagement;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LibraryManagementMain extends Application {
    @Override
    public void start(Stage primaryStage) {
        Label hello = new Label("Hello, JavaFX World!");
        StackPane root = new StackPane(hello);
        Scene scene = new Scene(root, 400, 200);
        primaryStage.setTitle("JavaFX Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
