package com.example.chatlog_project.models;

import java.util.ArrayList;
public class FriendRequest {
    private String status;
    private String senderId;
    private String userName;
    private String profileImageUrl; // URL to the sender's profile image, if available
    private boolean isAccepted; // Indicates whether the friend request is accepted or pending

    public FriendRequest() {
        // Default constructor required for Firebase
    }

    public FriendRequest(String status,String senderId, String userName, String profileImageUrl, boolean isAccepted) {
       this.status=status;
        this.senderId = senderId;
        this.userName = userName;
        this.profileImageUrl = profileImageUrl;
        this.isAccepted = isAccepted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter and Setter methods
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}
