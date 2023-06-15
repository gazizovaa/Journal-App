package com.example.journalapp.model;

import com.google.firebase.Timestamp;

public class Journal {
    private String titleOfPost;
    private String comments;
    private String imageUrl;

    private String userId;
    private String userName;
    private Timestamp timestamp;

    public Journal(){
        //empty constructor for Firebase which is mandatory
    }

    public Journal(String titleOfPost,String comments,String imageUrl,String userId,String userName,Timestamp timestamp){
        this.titleOfPost = titleOfPost;
        this.comments = comments;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.userName = userName;
        this.timestamp = timestamp;
    }

    //Getters and Setters

    public String getTitleOfPost() {
        return titleOfPost;
    }

    public void setTitleOfPost(String titleOfPost) {
        this.titleOfPost = titleOfPost;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
