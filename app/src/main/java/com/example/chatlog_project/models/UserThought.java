package com.example.chatlog_project.models;

import java.util.ArrayList;

public class UserThought {
    private String name, profileImage ;
    private String lastUpdated;
    private String thought;
    private String uploader_id;
    private String time;

    public UserThought(){

    }

    public UserThought(String uploader_id,String name, String profileImage, String lastUpdated, String thought, String time) {

        this.uploader_id = uploader_id;
        this.name = name;
        this.profileImage = profileImage;
        this.lastUpdated = lastUpdated;
        this.thought = thought;
        this.time = time;
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

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

}