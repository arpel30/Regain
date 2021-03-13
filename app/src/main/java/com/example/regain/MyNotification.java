package com.example.regain;

public class MyNotification {
    String app;
    String contact;
    String text;

    public MyNotification() {
    }

    public MyNotification(String app, String contact, String text) {
        this.app = app;
        this.contact = contact;
        this.text = text;
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
