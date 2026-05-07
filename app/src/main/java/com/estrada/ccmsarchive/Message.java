package com.estrada.ccmsarchive;

public class Message {
    private String text;
    private String time;
    private boolean isSentByMe;
    public Message() {
    }

    public Message(String text, String time, boolean isSentByMe) {
        this.text = text;
        this.time = time;
        this.isSentByMe = isSentByMe;
    }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public boolean isSentByMe() { return isSentByMe; }
    public void setSentByMe(boolean sentByMe) { isSentByMe = sentByMe; }
}