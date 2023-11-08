package com.aricilingiroglu.interimproject;

import java.util.Date;

public class Message {
    private String messageId;
    private String senderId;
    private String senderName;
    private String content;
    private Date timestamp;

    public Message() {
        // Default constructor required for Firestore
    }

    public Message(String messageId, String senderId, String senderName, String content, Date timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
    }

    // ... getters and setters
}
