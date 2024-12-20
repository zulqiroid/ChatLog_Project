package com.example.chatlog_project.models;

public class Status {
    private String imageURL;
    private long timestamp;
    private String uploader_id;

    public Status(){
    }

    public Status(String uploader_id,String imageURL, long timestamp) {
        this.imageURL = imageURL;
        this.timestamp = timestamp;
        this.uploader_id=uploader_id;
    }

    public String getUploader_id() {
        return uploader_id;
    }

    public void setUploader_id(String uploader_id) {
        this.uploader_id = uploader_id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
