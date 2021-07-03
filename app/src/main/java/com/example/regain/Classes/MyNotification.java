package com.example.regain.Classes;

public class MyNotification {
    private String app;
    private String contact;
    private String text;
    private long time;

    public MyNotification() {
    }

    public MyNotification(String app, String contact, String text, long time) {
        this.app = app;
        this.contact = contact;
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
