package com.estrada.ccmsarchive;

public class ChatUser {
    private String name;
    private String lastMessage;
    private String time;
    private String initials;
    private String uid;

    public ChatUser(String name, String lastMessage, String time, String initials, String uid) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
        this.initials = initials;
        this.uid = uid;
    }

    public ChatUser(String name, String initials, String uid) {
        this.name = name;
        this.initials = initials;
        this.uid = uid;
        this.lastMessage = "";
        this.time = "";
    }

    public String getName() { return name; }
    public String getLastMessage() { return lastMessage; }
    public String getTime() { return time; }
    public String getInitials() { return initials; }
    public String getUid() { return uid; }
}