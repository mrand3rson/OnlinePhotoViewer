package com.example.onlinephotoviewer.mvp.models;

/**
 * Created by Andrei on 30.03.2018.
 */

public class ApiResponse<T> {
    public int getStatus() {
        return status;
    }

    private int status;
    public T data;
}
