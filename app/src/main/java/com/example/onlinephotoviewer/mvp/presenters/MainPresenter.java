package com.example.onlinephotoviewer.mvp.presenters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.onlinephotoviewer.app.MyApplication;
import com.example.onlinephotoviewer.app.PhotoViewerApi;
import com.example.onlinephotoviewer.mvp.models.ApiImageIn;
import com.example.onlinephotoviewer.mvp.models.ApiImageOut;
import com.example.onlinephotoviewer.mvp.models.ApiResponse;
import com.example.onlinephotoviewer.mvp.models.SignUserOut;
import com.example.onlinephotoviewer.mvp.views.MainView;
import com.example.onlinephotoviewer.utils.Base64Formatter;
import com.example.onlinephotoviewer.utils.DateFormatter;

import java.io.File;
import java.util.Calendar;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Andrei on 30.03.2018.
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    public String getFileName() {
        return fileName;
    }

    private final String fileName = "photo.jpg";


    public Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }

    private File getOutputMediaFile(){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getPath());
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public void uploadImage(Location location) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        File file = getOutputMediaFile();
        Bitmap bmp = BitmapFactory.decodeFile(file.getPath(), options);

        String encodedImage = Base64Formatter.convertToBase64(bmp);
        getViewState().deleteTempPhoto(file);

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        ApiImageIn apiImageIn = new ApiImageIn(
                encodedImage,
                DateFormatter.dateToTimestamp(Calendar.getInstance()),
                latitude,
                longitude);

        PhotoViewerApi service = MyApplication.getRetrofit().create(PhotoViewerApi.class);
        Call<ApiResponse<ApiImageOut>> call = service.addImage(MyApplication.getUserInfo().getToken(), apiImageIn);
        call.enqueue(new Callback<ApiResponse<ApiImageOut>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<ApiImageOut>> call, @NonNull Response<ApiResponse<ApiImageOut>> response) {
                if (response.body() != null) {
                    ApiImageOut apiImage = response.body().data;
                    getViewState().onSuccessUploading(apiImage);
                } else {
                    getViewState().onFailedUploading("");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<ApiImageOut>> call, @NonNull Throwable t) {
                getViewState().onFailedUploading(t.getMessage());
            }
        });
    }

    public void saveLastSession(final SignUserOut user) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SignUserOut.class);
        realm.copyToRealm(user);
        realm.commitTransaction();
    }
}
