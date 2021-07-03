package com.example.regain.Classes;

public class Message {
    private long time;
    private String text;

    public Message(long time, String text) {
        this.time = time;
        this.text = text;
    }

    public Message() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
