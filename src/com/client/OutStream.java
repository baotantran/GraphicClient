package com.client;

import com.message.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

public class OutStream {
    private ObjectOutputStream stream;

    public OutStream(ObjectOutputStream stream) {
        this.stream = stream;
    }

    public synchronized void send(Message message) {
        try{
            stream.writeObject(message);
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
