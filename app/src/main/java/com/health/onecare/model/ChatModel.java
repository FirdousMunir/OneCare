package com.health.onecare.model;

public class ChatModel {

    String id,message, sentById,receiveById, senderName, sentAt, sentDate;

    public ChatModel(String id, String message, String sentById, String receiveById, String senderName, String sentAt, String sentDate) {
        this.id = id;
        this.message = message;
        this.sentById = sentById;
        this.receiveById = receiveById;
        this.senderName = senderName;
        this.sentAt = sentAt;
        this.sentDate = sentDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentById() {
        return sentById;
    }

    public void setSentById(String sentById) {
        this.sentById = sentById;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public String getReceiveById() {
        return receiveById;
    }

    public void setReceiveById(String receiveById) {
        this.receiveById = receiveById;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }
}
