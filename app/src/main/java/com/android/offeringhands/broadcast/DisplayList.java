package com.android.offeringhands.broadcast;

public class DisplayList {
    String name;
    String description;
    String broadcastId;
    String broadcastSubject;
    String imagePath;

    public DisplayList(String name, String description, String broadcastId, String subject, String imagePath) {
        this.name = name;
        this.description = description;
        this.broadcastId = broadcastId;
        this.broadcastSubject = subject;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public String getBroadcastSubject() {
        return broadcastSubject;
    }

    public String getImagePath() {
        return imagePath;
    }
}