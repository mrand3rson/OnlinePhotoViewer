package com.example.onlinephotoviewer.mvp.presenters;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.onlinephotoviewer.app.MyApplication;
import com.example.onlinephotoviewer.app.PhotoViewerApi;
import com.example.onlinephotoviewer.mvp.models.ApiCommentOut;
import com.example.onlinephotoviewer.mvp.models.ApiImageOut;
import com.example.onlinephotoviewer.mvp.models.ApiResponse;
import com.example.onlinephotoviewer.mvp.views.PhotosView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
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
                    if (response.body().data.size() != 0) {
                        getViewState().onHasConnectedComments();
                        try {
                            if (!deleteComments(service, apiImage, response.body().data)) {
                                getViewState().onFailedQuery("Some comments failed to delete");
                                return;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }
                    }

                    deletePhoto(service, apiImage);
                } else {
                    getViewState().onFailedQuery("Error while getting comments");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<ApiCommentOut>>> call,
                                  @NonNull Throwable t) {

            }
        });
    }

    private boolean deleteComments(final PhotoViewerApi service,
                                final ApiImageOut apiImage,
                                final List<ApiCommentOut> comments) throws InterruptedException {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Boolean> callable = new Callable<Boolean>() {
            @Override
            public Boolean call() {
                for (ApiCommentOut comment : comments) {
                    if (!deleteComment(service, apiImage, comment)) {
                        return false;
                    }
                }
                return true;
            }
        };
        boolean deletedAll = false;

        Future<Boolean> future = executor.submit(callable);
        try {
            deletedAll = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdown();
        return deletedAll;
    }

    private boolean deleteComment(PhotoViewerApi service,
                               ApiImageOut apiImage,
                               ApiCommentOut apiComment) {

        Call<ApiResponse<ApiCommentOut>> call =
                service.deleteComment(MyApplication.getUserInfo().getToken(),
                        apiComment.getId(),
                        apiImage.getId());
        Response<ApiResponse<ApiCommentOut>> response;
        try {
            response = call.execute();
            return response.errorBody() == null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

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
