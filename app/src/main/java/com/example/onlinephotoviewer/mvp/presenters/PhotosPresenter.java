package com.example.onlinephotoviewer.mvp.presenters;

import android.support.annotation.NonNull;
import android.support.v4.widget.SearchViewCompat;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.onlinephotoviewer.app.MyApplication;
import com.example.onlinephotoviewer.app.PhotoViewerApi;
import com.example.onlinephotoviewer.mvp.models.ApiCommentOut;
import com.example.onlinephotoviewer.mvp.models.ApiImageOut;
import com.example.onlinephotoviewer.mvp.models.ApiResponse;
import com.example.onlinephotoviewer.mvp.views.PhotosView;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.internal.http2.Http2Connection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Andrei on 30.03.2018.
 */

@InjectViewState
public class PhotosPresenter extends MvpPresenter<PhotosView> {

    public List<ApiImageOut> getData() {
        return data;
    }

    private List<ApiImageOut> data = new ArrayList<>();
    private List<ApiCommentOut> comments;

    public PhotosPresenter() {

    }

    public void loadPhotosFromServer() {
        PhotoViewerApi service = MyApplication.getRetrofit().create(PhotoViewerApi.class);
        Call<ApiResponse<List<ApiImageOut>>> call = service.getImages(MyApplication.getUserInfo().getToken(), 0);
        call.enqueue(new Callback<ApiResponse<List<ApiImageOut>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<ApiImageOut>>> call,
                                   @NonNull Response<ApiResponse<List<ApiImageOut>>> response) {
                if (response.errorBody() == null) {
                    ApiResponse<List<ApiImageOut>> out = response.body();
                    getViewState().setupAdapter(out.data);
                    updateImagesInDatabase(out.data);
                    data = out.data;
                    getViewState().onSuccessQuery();
                } else {
                    getViewState().onFailedQuery(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<ApiImageOut>>> call, @NonNull Throwable t) {

                getViewState().onFailedQuery(null);
            }
        });
    }

    public void loadImagesFromDatabase() {
        final Realm realm = Realm.getDefaultInstance();
        getViewState().startLoadingData();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm bgRealm) {
                RealmList<ApiImageOut> list = new RealmList<>();
                RealmResults<ApiImageOut> apiImages = bgRealm.where(ApiImageOut.class).findAll();
                data = bgRealm.copyFromRealm(apiImages);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                getViewState().setupAdapter(data);
                getViewState().finishLoadingData();
                realm.close();
            }
        });
    }

    public void checkCommentsAndDeletePhoto(final ApiImageOut apiImage) {
        final PhotoViewerApi service = MyApplication.getRetrofit().create(PhotoViewerApi.class);
        service.getComments(MyApplication.getUserInfo().getToken(),
                apiImage.getId(),
                0).enqueue(new Callback<ApiResponse<List<ApiCommentOut>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<ApiCommentOut>>> call,
                                   @NonNull Response<ApiResponse<List<ApiCommentOut>>> response) {
                if (response.errorBody() == null) {
                    if (response.body().data.size() == 0) {
                        deletePhoto(service, apiImage);
                    } else {
                        getViewState().onHasConnectedComments();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<ApiCommentOut>>> call,
                                  @NonNull Throwable t) {

            }
        });
    }

    private void deletePhoto(final PhotoViewerApi service,
                             final ApiImageOut apiImage) {

        service.deleteImage(MyApplication.getUserInfo().getToken(),
                apiImage.getId()).enqueue(new Callback<ApiResponse<ApiImageOut>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<ApiImageOut>> call,
                                   @NonNull Response<ApiResponse<ApiImageOut>> response) {

                if (response.errorBody() == null) {
                    getViewState().onSuccessQuery();

                    deleteImageFromDatabase(apiImage);
                    getViewState().refreshPhotosOnDelete(apiImage);
                } else {

                    getViewState().onFailedQuery(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<ApiImageOut>> call,
                                  @NonNull Throwable t) {
                getViewState().onFailedQuery(null);
            }
        });
    }

    public void insertImageIntoDatabase(final ApiImageOut apiImage) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(apiImage);
        realm.commitTransaction();
    }

    private void updateImagesInDatabase(List<ApiImageOut> data) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(data);
        realm.commitTransaction();
    }

    private void deleteImageFromDatabase(final ApiImageOut apiImage) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(ApiImageOut.class).equalTo("id", apiImage.getId())
                .findFirst().deleteFromRealm();
        realm.commitTransaction();
    }
}
