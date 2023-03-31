package com.example.projektwindyjavafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class HelloApplication extends Application {

    static Pane root;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        root = fxmlLoader.load();

        Scene scene = new Scene(root, 1600, 900);
        stage.setTitle("Animacja windy");
        stage.setScene(scene);

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });

        stage.show();

        Main main = new Main();     //uruchomienie watka zajmujacego sie programem
        main.start();
    }

    public static void main(String[] args) {
        launch();
    }
}