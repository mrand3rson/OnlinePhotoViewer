package com.example.onlinephotoviewer.utils;

import android.util.Log;

import com.example.onlinephotoviewer.mvp.models.response.ApiResponseError;
import com.example.onlinephotoviewer.mvp.models.response.ApiResponseErrorAuth;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

import static com.example.onlinephotoviewer.app.MyApplication.getRetrofit;

/**
 * Created by Andrei on 04.04.2018.
 */

public class ErrorResponseCreator {

    private final static String LABEL_DEBUG = "Error Response debug";
    private final static String LOG_EXCEPTION = "Exception while converting <error response>";

    public final static String LOG_STATUS = "Status code %d: %s";


    public static ApiResponseError create(Response response) {
        Converter<ResponseBody, ApiResponseError> errorConverter =
                getRetrofit().responseBodyConverter(ApiResponseError.class, new Annotation[0]);
        try {
            return errorConverter.convert(response.errorBody());

        } catch (IOException e) {
            Log.e(LABEL_DEBUG, LOG_EXCEPTION);
        }

        return null;
    }

    public static ApiResponseErrorAuth createForAuth(Response response, String LABEL_DEBUG) {
        Converter<ResponseBody, ApiResponseErrorAuth> errorConverter =
                getRetrofit().responseBodyConverter(ApiResponseErrorAuth.class, new Annotation[0]);
        try {
            return errorConverter.convert(response.errorBody());

        } catch (IOException e) {
            Log.e(LABEL_DEBUG, LOG_EXCEPTION);
        }

        return null;
    }
}
