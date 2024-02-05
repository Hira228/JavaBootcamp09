package edu.school21.sockets.models;

import java.net.Socket;
import java.util.*;

public class Room {
    private static Long id;
    static {
        id = 0L;
    }
    private final String name;
    private final List<Socket> roomAttendees = Collections.synchronizedList(new ArrayList<>());
    private final Stack<String> messagesRoom = new Stack<>();

    public Room(String name) {
        this.name = name;
        id++;
    }

    public Long getId() {
        return id;
    }

    public void addUserInRoom(Socket socketUser) {
        roomAttendees.add(socketUser);
    }

    public void addMessage(String message) {
        if(messagesRoom.size() > 30) messagesRoom.pop();
        messagesRoom.add(message);
    }

    public List<Socket> getRoomAttendees() {
        return roomAttendees;
    }

    public Stack<String> getMessagesRoom() {
        return messagesRoom;
    }

    public String getName() {
        return name;
    }
}
