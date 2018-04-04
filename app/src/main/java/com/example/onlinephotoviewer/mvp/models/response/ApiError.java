package com.example.onlinephotoviewer.mvp.models.response;

/**
 * Created by Andrei on 04.04.2018.
 */

public class ApiError {
    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    private String field;
    private String message;
}
