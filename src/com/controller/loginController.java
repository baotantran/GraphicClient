package com.controller;

import com.jfoenix.controls.JFXTextField;
import com.controller.Controller;
import com.client.Client;
import javafx.fxml.FXML;
import com.interfaces.ApplicationInterface;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.regex.Pattern;

public class loginController {

    @FXML
    private JFXTextField clientName;
    @FXML
    private JFXTextField serverAddress;
    @FXML
    private JFXTextField portNumber;

    // Default values
    private String name = "Admin";
    private String address = "LocalHost";
    private int port = 5678;

    private static Controller controller;

    @FXML
    public void login() throws Exception {
        getUserInput();
        ApplicationInterface.getInstance().close();
        Stage newStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/interfaces/MainInterface.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        Controller controller = fxmlLoader.getController();
        // pass address, port, controller and client name to client instance
        Client client = new Client(address, port, controller, name);
        Thread t = new Thread(client);
        t.start();
        Scene scene = new Scene(root, 500, 600);
        newStage.setResizable(false);
        newStage.setTitle("Synchronous Video Chat");
        newStage.setScene(scene);
        newStage.show();
    }

    // Check user input format
    // Use default value otherwise
    private void getUserInput() {
        if(clientName.getLength() > 0) {
            name = clientName.getText();
        }
        String port = portNumber.getText();
        if(port.matches("\\d+")
                && port.length() >= 4
                && port.length() <= 5) {
            int tempPort = Integer.parseInt(portNumber.getText());
            if(tempPort >= 1024 && tempPort <= 65535) {
                this.port = tempPort;
            }
        }
        String tempAddress = serverAddress.getText();
        if(!tempAddress.equalsIgnoreCase("LocalHost") && tempAddress.length() > 0) {
            String[] numb = tempAddress.split(".");
            boolean flag = true;
            if(numb.length == 4) {
                for (String n : numb) {
                    if(!n.matches("\\d+")
                            || n.length() > 3
                            || n.length() == 0){
                        flag = false;
                    } if (flag) {
                        int index = Integer.parseInt(n);
                        if(index > 255 || index < 0) {
                            flag = false;
                        }
                    }
                }
                if(flag) {
                    address = tempAddress;
                }
            }
        }
    }
}
