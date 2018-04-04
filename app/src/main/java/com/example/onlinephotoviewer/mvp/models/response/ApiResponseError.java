package com.example.onlinephotoviewer.mvp.models.response;

/**
 * Created by Andrei on 04.04.2018.
 */

public class ApiResponseError extends BaseResponse {
    public String getError() {
        return error;
    }

    private String error;
}
