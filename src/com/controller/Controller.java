package com.controller;

import com.client.Client;
import com.jfoenix.controls.JFXSlider;
import com.message.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.media.MediaView;

public class Controller {

    @FXML
    private TextField userIn;
    @FXML
    private TextArea textArea;
    @FXML
    private JFXSlider timeSlider;
    @FXML
    private MediaView mediaView;



    public void showNotification(final String note) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                textArea.appendText(note);
            }
        });
    }

    // Show received message in text area
    public void showInMessage(final Message message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                textArea.appendText(message.getStringMessage() + "\n");
            }
        });
    }

    // Show message send to server
    public void showOutMessage(final Message message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                textArea.appendText(message.getStringMessage() + "\n");
                userIn.setText("");
            }
        });
    }

    // Send simple string message
    public void sendStringMessage(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                userIn.setText("");
            }
        });
        Message message = new Message();
        String text = userIn.getText();
        message.setStringMessage(Client.clientName + ": " + userIn.getText());
        Client.sendMessage(message);
    }

    public void pressPlayButton() {
        //TODO
    }
}
