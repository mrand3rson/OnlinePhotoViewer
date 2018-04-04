package com.example.onlinephotoviewer.mvp.models.response;

/**
 * Created by Andrei on 04.04.2018.
 */

public abstract class BaseResponse {
    public int getStatus() {
        return status;
    }

    protected int status;
}
