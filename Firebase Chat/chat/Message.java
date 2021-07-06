package com.example.firebasechatapp.example.chatmodule.chat;

public class Message {

    String message;
    String type;
    String from;
    String messageKey;
    boolean delete;
    long timestamp;
    int selectedPosition = -1;

    public Message(String message, String type, String from, long timestamp, String messageKey, boolean delete) {
        this.message = message;
        this.type = type;
        this.from = from;
        this.timestamp = timestamp;
        this.messageKey = messageKey;
        this.delete = delete;
    }

    public Message() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public boolean getDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
}
