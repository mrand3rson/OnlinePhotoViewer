package com.example.onlinephotoviewer.mvp.presenters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.onlinephotoviewer.app.MyApplication;
import com.example.onlinephotoviewer.app.PhotoViewerApi;
import com.example.onlinephotoviewer.mvp.models.ApiImageIn;
import com.example.onlinephotoviewer.mvp.models.ApiImageOut;
import com.example.onlinephotoviewer.mvp.models.response.ApiResponseError;
import com.example.onlinephotoviewer.mvp.models.response.ApiResponseSuccess;
import com.example.onlinephotoviewer.mvp.views.MainView;
import com.example.onlinephotoviewer.utils.Base64Formatter;
import com.example.onlinephotoviewer.utils.DateFormatter;
import com.example.onlinephotoviewer.utils.ErrorResponseCreator;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Andrei on 30.03.2018.
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    public static String getLabelDebug() {
        return LABEL_DEBUG;
    }

    private final static String LABEL_DEBUG = "Main debug";


    public void uploadImage(Bitmap originalBitmap, Location location) {
        Bitmap smallImage = getSmallBitmap(originalBitmap);
        String encodedImage = Base64Formatter.convertToBase64(smallImage);

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        ApiImageIn apiImageIn = new ApiImageIn(
                encodedImage,
                DateFormatter.dateToTimestamp(Calendar.getInstance()),
                latitude,
                longitude);

        PhotoViewerApi service = MyApplication.getRetrofit().create(PhotoViewerApi.class);
        Call<ApiResponseSuccess<ApiImageOut>> call = service.addImage(MyApplication.getUserInfo().getToken(), apiImageIn);
        call.enqueue(new Callback<ApiResponseSuccess<ApiImageOut>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseSuccess<ApiImageOut>> call,
                                   @NonNull Response<ApiResponseSuccess<ApiImageOut>> response) {
                if (response.isSuccessful()) {
                    ApiImageOut apiImage = response.body().data;
                    getViewState().onSuccessUploading(apiImage);
                } else {
                    ApiResponseError errorResponse = ErrorResponseCreator.create(response);
                    if (errorResponse != null) {
                        getViewState().onErrorResponse(errorResponse.getError());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseSuccess<ApiImageOut>> call,
                                  @NonNull Throwable t) {
                Log.d(LABEL_DEBUG, t.getMessage());
                getViewState().onFailedUploading(t.getMessage());
            }
        });
    }

    private Bitmap getSmallBitmap(Bitmap originalBitmap) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        originalBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bmpArray = stream.toByteArray();

        return BitmapFactory.decodeByteArray(bmpArray,
                0, bmpArray.length, options);
    }
}
