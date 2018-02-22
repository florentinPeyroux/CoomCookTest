package com.eservices.floamnx.coomcook.chat;

public class FriendDetails {

    private String userId;
    private String userName;
    private String userPicture;

    public FriendDetails(){}

    public FriendDetails(String userId, String userName, String userPicture) {
        this.userId = userId;
        this.userName = userName;
        this.userPicture = userPicture;
    }

    public String getUserId() {
        return userId;
    }


    public String getUserName() {
        return userName;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

}