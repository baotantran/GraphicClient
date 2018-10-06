package com.interfaces;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationInterface extends Application {

    public static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("Synchronous Video Chat");
        primaryStage.setScene(new Scene(root, 300, 200));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static Stage getInstance() {return stage;}

    public static void main(String[] args) {
        launch(args);
    }
}
