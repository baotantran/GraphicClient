package com.controller;

import com.client.Client;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.message.Message;
import com.message.Type;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.media.MediaView;
import javafx.scene.media.*;
import javafx.scene.media.MediaPlayer.*;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.client.Client.clientName;
import static com.client.Client.sendMessage;

public class Controller {

    @FXML
    private JFXTextField userIn;
    @FXML
    private JFXTextArea textArea;
    @FXML
    private JFXSlider timeSlider;
    @FXML
    private MediaView mediaView;
    @FXML
    private JFXButton playButton;
    @FXML
    private JFXTextField linkField;

    public static MediaPlayer player;
    public static Status status;
    private static Duration duration;
    private Duration current;
    public static boolean playerExist = false;

    public void showNotification(final String note) {
        Platform.runLater(() -> {
            textArea.appendText(note);
        });
    }

    // Show received message in text area
    public void showInMessage(final Message message) {
        Platform.runLater(() -> {
            textArea.appendText(message.getStringMessage() + "\n");
        });
    }

    // Show message send to server
    public void showOutMessage(final Message message) {
        Platform.runLater(() -> {
            textArea.appendText(message.getStringMessage() + "\n");
            userIn.setText("");
        });
    }

    // Send simple string message
    public void sendStringMessage(){
        Message message = new Message();
        String text = userIn.getText();
        if(text.equalsIgnoreCase("end")) {
            message.setType(Type.TERMINATE);
            message.setStringMessage("end");
        } else {
            message.setType(Type.NORMAL);
            message.setStringMessage(Client.clientName + ": " + text);
        }
        sendMessage(message);
        Platform.runLater(() -> {
            userIn.setText("");
        });
    }

    @FXML
    private void sendLinkToServer() {
        Message message = new Message();
        message.setType(Type.LINK);
        message.setStringMessage(linkField.getText());
        sendMessage(message);
        Platform.runLater(() -> {
            linkField.setText("");
        });
    }

    public void setupMedia(String link) {
        if(playerExist) {
            player.dispose();
        }
        playerExist = true;
        Media media = new Media(link);
        player = new MediaPlayer(media);
        player.setAutoPlay(false);
        setup();
        mediaView.setMediaPlayer(player);
    }

    public void setup() {
        player.setAutoPlay(false);
        player.setOnPlaying(() -> {
            status = Status.PLAYING;
        });

        player.setOnHalted(() -> {
            status = Status.HALTED;
        });

        player.setOnPaused(() -> {
            status = Status.PAUSED;
        });

        player.currentTimeProperty().addListener((observable) -> {
            updateTime();
        });

        timeSlider.valueProperty().addListener((obser, oldValue, newValue) -> {
                updateTime();
        });

        player.setOnReady(() -> {
            Message message = new Message();
            message.setStringMessage("media is ready");
            message.setStatus(Status.READY);
            message.setType(Type.STATUS);
            status = Status.READY;
            sendMessage(message);
            duration = player.getMedia().getDuration();
        });

    }

    private void updateTime() {
        if(timeSlider != null) {
            Platform.runLater(() -> {
                current = player.getCurrentTime();
                if(!timeSlider.isDisable()
                        && duration.greaterThan(Duration.ZERO)
                        && !timeSlider.isValueChanging()) {
                    timeSlider.setValue(current.divide(duration.toMillis()).toMillis() * 100);
                } else if (!timeSlider.isDisable() &&
                        duration.greaterThan(Duration.ZERO) &&
                        timeSlider.isValueChanging()) {
                    double time = duration.toMillis() * timeSlider.getValue() / 100;
                    player.seek(new Duration(time));
                    Client.sendUpdateTime(time);
                }
            });
        }
    }

    public void updateMediaTime(double time) {
        Platform.runLater(() -> {
            if(duration.greaterThan(Duration.ZERO)) {
                player.seek(new Duration(time));
            }
        });
    }


    public void playMedia() {
        Platform.runLater(() -> {
            if(status == Status.HALTED || status == Status.UNKNOWN) {
                showNotification("Can't open video \n");
                return;
            }
            if(status == Status.READY || status == Status.PAUSED || status == Status.STOPPED) {
                player.play();
                playButton.setText("||");
                Client.sendTimeRequest();
            } else {
                player.pause();
                playButton.setText(">");
            }
        });
    }
}
