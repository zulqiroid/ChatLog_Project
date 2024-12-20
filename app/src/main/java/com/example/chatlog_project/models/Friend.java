package com.example.chatlog_project.models;

public class Friend {
    private String id;
    private String name;
    private String profilePictureUrl;

    public Friend() {
    }

    public Friend(String id, String name, String profilePictureUrl) {
        this.id = id;
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}

