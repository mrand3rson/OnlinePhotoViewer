package com.example.onlinephotoviewer.mvp.models;

/**
 * Created by Andrei on 31.03.2018.
 */

public class ApiCommentOut {
    public int getDate() {
        return date;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    private int date;
    private int id;
    private String text;

    public ApiCommentOut(int date, int id, String text) {
        this.date = date;
        this.id = id;
        this.text = text;
    }
}
