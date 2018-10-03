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

public class Client implements Runnable{
    private static Socket connection;
    private static ObjectInputStream inMessage;
    private static ObjectOutputStream outMessage;
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
        try {
            outMessage.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send initial message contain type and name of client
    public static void sendInitial(ObjectOutputStream output) throws IOException{
        Message first = new Message();
        first.setType(Type.FIRST);
        first.setName(clientName);
        output.writeObject(first);
    }

    @Override
    public void run(){
        try {
            outMessage = new ObjectOutputStream(connection.getOutputStream());
            inMessage = new ObjectInputStream(connection.getInputStream());
            sendInitial(outMessage);
            controller.showNotification("Stream setup successfully \n");
            while (!connection.isClosed()) {
                Message message = (Message) inMessage.readObject();
                if(message.getStringMessage().equalsIgnoreCase("end")) {
                    closeConnection();
                    controller.showNotification("end from server");
                    break;
                }
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
                Status status = message.getStatus();
                if(!controller.playerExist && (status == Status.READY
                        || status == Status.PLAYING
                        || status == Status.PAUSED)) {
                    prepareMedia();
                }
                break;
            case TIME:
                if(controller.playerExist) {
                    updateMediaTime(message.getTime());
                    System.out.println("update time");
                }
                break;
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

    public void prepareMedia() {
        controller.setupMedia();
    }

    public void updateMediaTime(double time){
        controller.updateMediaTime(time);
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
