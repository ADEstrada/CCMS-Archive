package com.estrada.ccmsarchive;

public class ChatUser {
    private String name;
    private String lastMessage;
    private String time;
    private String initials;

    public ChatUser(String name, String lastMessage, String time, String initials) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
        this.initials = initials;
    }

    public String getName() { return name; }
    public String getLastMessage() { return lastMessage; }
    public String getTime() { return time; }
    public String getInitials() { return initials; }
}