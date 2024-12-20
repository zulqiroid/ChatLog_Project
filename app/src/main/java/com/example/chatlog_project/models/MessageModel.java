package com.example.chatlog_project.models;

public class MessageModel {
    public String sender_id, message, imageUrl;
    public String timestamp;
    public  int reaction=-1;
    public String message_id;
    public String type;
    public String receiver_id;
    public String time;
    public String date;
    public String duration;

    public MessageModel(String sender_id, String receiver_id, String message, String duration, String date, String time, String timestamp, String type) {
        this.sender_id = sender_id;
        this.message = message;
        this.timestamp = timestamp;
        this.reaction = reaction;
        this.message_id = message_id;
        this.type = type;
        this.receiver_id = receiver_id;
        this.time = time;
        this.date = date;
        this.duration = duration;
    }

    public MessageModel() {
    }

    public MessageModel(String sender_id, String receiver_id, String message,String time, String date, String timestamp, String type) {
        this.sender_id = sender_id;
        this.message = message;
        this.timestamp = timestamp;
        this.reaction = reaction;
        this.message_id = message_id;
        this.imageUrl=imageUrl;
        this.type=type;
        this.receiver_id=receiver_id;
        this.time=time;
        this.date=date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSender() {
        return sender_id;
    }

    public void setSender(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String  timestamp) {
        this.timestamp = timestamp;
    }

    public int getReaction() {
        return reaction;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getTime() {
        return time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
