package com.example.onlinephotoviewer.mvp.models.response;

import java.util.ArrayList;

/**
 * Created by Andrei on 04.04.2018.
 */

public class ApiResponseErrorAuth extends ApiResponseError {
    public ArrayList<ApiError> getErrorsList() {
        return valid;
    }

    private ArrayList<ApiError> valid;
}
