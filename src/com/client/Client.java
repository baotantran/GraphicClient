package com.client;

import com.controller.Controller;
import com.message.Message;
import com.message.Type;

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
            controller.showNotification("Stream setup successfully \n");
            sendInitial(outMessage);
            while (!connection.isClosed()) {
                Message message = (Message) inMessage.readObject();
                if(message.getStringMessage().equalsIgnoreCase("end")) {
                    closeConnection();
                    controller.showNotification("end from server");
                    break;
                }
                controller.showInMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
