package com.virtixstudio.viyubeta;

public class Message {
    private String senderId;
    private String receiverId;
    private String message;
    private long timestamp;
    private boolean isRead;

    public Message() {
        // Requis par Firebase
    }

    public Message(String senderId, String receiverId, String message, long timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = false; // Par défaut, un message envoyé n'est pas encore lu
    }

    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    public boolean isIsRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
