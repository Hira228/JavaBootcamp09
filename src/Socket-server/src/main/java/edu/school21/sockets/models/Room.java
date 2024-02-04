package edu.school21.sockets.models;

import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Room {
    public String name;
    public final CopyOnWriteArrayList<Socket> roomAttendees = new CopyOnWriteArrayList<>();
    public Stack<String> messagesRoom = new Stack<>();


    public Room(String name) {
        this.name = name;
    }

    public void addUserInRoom(Socket socketUser) {
        roomAttendees.add(socketUser);
    }

    public void addMessage(String message) {
        if(messagesRoom.size() > 30) messagesRoom.pop();
        messagesRoom.add(message);
    }

    public String getName() {
        return name;
    }
}
