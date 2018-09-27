package com.controller;

import com.jfoenix.controls.JFXTextField;
import com.controller.Controller;
import com.client.Client;
import javafx.fxml.FXML;
import com.interfaces.ApplicationInterface;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class loginController {

    @FXML
    private JFXTextField clientName;

    private static Controller controller;

    @FXML
    public void login() throws Exception {
        String name = clientName.getText();
        ApplicationInterface.getInstance().close();
        Stage newStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/interfaces/MainInterface.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Controller controller = fxmlLoader.getController();
        // pass address, port, controller and client name to client instance
        Client client = new Client("LocalHost", 5678, controller, clientName.getText());
        Thread t = new Thread(client);
        t.start();
        Scene scene = new Scene(root, 500, 600);
        newStage.setResizable(false);
        newStage.setTitle("Synchronous Video Chat");
        newStage.setScene(scene);
        newStage.show();
    }
}
