package com.message;

import java.io.Serializable;
import com.message.Type;
import javafx.scene.media.MediaPlayer.Status;

public class Message implements Serializable {
    private static final long serialVersionUID = 0x0505;
    private String stringMessage;
    private double time = 0;
    private Status status;
    private String name;
    private Type type;

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStringMessage(String stringMessage) {
        this.stringMessage = stringMessage;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getStringMessage() {
        return stringMessage;
    }

    public double getTime() {
        return time;
    }
}
