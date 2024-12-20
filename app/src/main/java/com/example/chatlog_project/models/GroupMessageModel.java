package com.example.chatlog_project.models;

public class GroupMessageModel {
    public String sender_id, message, sender_name;
    public String timestamp;
    public String message_id;
    public String type;
    public String receiver_group_id;
    public String time;
    public String date;

    public GroupMessageModel(String sender_id, String sender_name, String receiver_group_id, String message, String timestamp, String time, String date, String type) {
        this.sender_id = sender_id;
        this.message = message;
        this.sender_name = sender_name;
        this.timestamp = timestamp;
        this.type = type;
        this.receiver_group_id = receiver_group_id;
        this.time = time;
        this.date = date;
    }

    public GroupMessageModel() {
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public String getReceiver_group_id() {
        return receiver_group_id;
    }

    public void setReceiver_group_id(String receiver_group_id) {
        this.receiver_group_id = receiver_group_id;
    }

    public String getTime() {
        return time;
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
