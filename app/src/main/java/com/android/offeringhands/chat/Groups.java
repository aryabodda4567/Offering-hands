package com.android.offeringhands.chat;

public class Groups {
    String name;
    String broadcastId;
    String imageUrl;

    public Groups(String name, String broadcastId, String imageUrl) {
        this.name = name;
        this.broadcastId = broadcastId;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
