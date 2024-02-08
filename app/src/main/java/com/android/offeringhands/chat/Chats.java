package com.android.offeringhands.chat;

public class Chats {
    String broadcastName;
    String message;
    String id;

    public Chats(String broadcastName, String message, String id) {
        this.broadcastName = broadcastName;
        this.message = message;
        this.id = id;
    }

    public String getBroadcastName() {
        return broadcastName;
    }

    public String getMessage() {
        return message;
    }

    public String id() {
        return id;
    }
}
