package com.example.chatlog_project.models;

import java.util.ArrayList;

public class UserStatus {
    private String name, profileImage ;
    private long lastUpdated;
    private ArrayList<Status> statuses = new ArrayList<>(); // Initialize here
    private String uploader_id;

    public UserStatus(){

    }

    public UserStatus(String uploader_id,String name, String profileImage, long lastUpdated, ArrayList<Status> statuses) {

        this.uploader_id = uploader_id;
        this.name = name;
        this.profileImage = profileImage;
        this.lastUpdated = lastUpdated;
        this.statuses = statuses;
    }

    public String getUploader_id() {
        return uploader_id;
    }

    public void setUploader_id(String uploader_id) {
        this.uploader_id = uploader_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }
}
