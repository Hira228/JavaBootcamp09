package edu.school21.sockets.models;

public class Message {
    private final String message;
    private final Long fromId;
    private final Long roomId;

    public Message(String message, Long fromId, Long roomId) {
        this.message = message;
        this.fromId = fromId;
        this.roomId = roomId;
    }

    public String getMessage() {
        return message;
    }

    public Long getFromId() {
        return fromId;
    }

    public Long getRoomId() {
        return roomId;
    }
}
