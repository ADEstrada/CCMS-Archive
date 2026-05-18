package com.estrada.ccmsarchive;

import com.google.firebase.Timestamp;

public class NotificationModel {
    private String userID;
    private String title;
    private String message;
    private String instructor;
    private String status;
    private Timestamp timestamp;
    private String notificationId;

    public NotificationModel() {}

    public NotificationModel(String userID, String title, String message, String instructor, String status, Timestamp timestamp, String notificationId) {
        this.userID = userID;
        this.title = title;
        this.message = message;
        this.instructor = instructor;
        this.status = status;
        this.timestamp = timestamp;
        this.notificationId = notificationId;
    }

    // Getters
    public String getuserID() { return userID; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getInstructor() { return instructor; }
    public String getStatus() { return status; }
    public Timestamp getTimestamp() { return timestamp; }
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }
}