package com.client;

import com.controller.Controller;
import com.message.Message;
import com.message.Type;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client implements Runnable{
    private static Socket connection;
    private static ObjectInputStream inMessage;
    private static OutStream outMessage;
    private static Controller controller;
    public static String clientName;

    public Client(String address, int port, Controller controller, String name) {
        clientName = name;
        this.controller = controller;
        try {
            controller.showNotification("Trying connection to Server + \n");
            connection = new Socket(address, port);
            controller.showNotification("Connected to " + connection.getRemoteSocketAddress() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send Client message
    public static void sendMessage(Message message) {
        outMessage.send(message);
    }

    // Send initial message contain type and name of client
    public static void sendInitial() throws IOException{
        Message first = new Message();
        first.setType(Type.FIRST);
        first.setName(clientName);
        sendMessage(first);
    }

    public static void sendUpdateTime(double time) {
        Message message = new Message();
        message.setStatus(controller.player.getStatus());
        message.setType(Type.TIME);
        message.setStringMessage("Client Time");
        message.setTime(time);
        sendMessage(message);
    }

    @Override
    public void run(){
        try {
            outMessage = new OutStream(new ObjectOutputStream(connection.getOutputStream()));
            inMessage = new ObjectInputStream(connection.getInputStream());
            sendInitial();
            controller.showNotification("Stream setup successfully \n");
            while (!connection.isClosed()) {
                Message message = (Message) inMessage.readObject();
                interpreter(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void interpreter(Message message) {
        Type type = message.getType();
        switch (type) {
            case NORMAL:
                controller.showInMessage(message);
                break;
            case STATUS:
                prepareMedia(message);
                break;
            case TIME:
                updateMediaTime(message);
                break;
            case COMMAND:
                followCommand(message);
                break;
            case TERMINATE:
                closeConnection();
                controller.showNotification("end from server");
                break;
        }
    }

    private void followCommand(Message message) {
        if(controller.playerExist) {
            String command = message.getStringMessage();
            Status status = controller.player.getStatus();
            System.out.println(command);
            if(command.equalsIgnoreCase("play") && status != Status.PLAYING) controller.playMedia();
            else if(command.equalsIgnoreCase("paused") && status != Status.PAUSED) controller.playMedia();
        }
    }

    public static void sendTimeRequest(){
        if(controller.playerExist) {
            Message message = new Message();
            message.setType(Type.REQUEST);
            message.setStatus(controller.player.getStatus());
            message.setStringMessage("Request Server player current time");
            sendMessage(message);
            System.out.println("Send time request");

        }
    }

    public void prepareMedia(Message message) {
        Status status = message.getStatus();
        if(status == Status.READY
                || status == Status.PLAYING
                || status == Status.PAUSED) {
            controller.setupMedia(message.getStringMessage());
        }
    }

    public void updateMediaTime(Message message){
        if(controller.playerExist) {
            controller.updateMediaTime(message.getTime());
            System.out.println("update time");
        }
    }

    public static void closeConnection() {
        try{
            inMessage.close();
            outMessage.close();
            connection.isClosed();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
