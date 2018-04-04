package com.example.onlinephotoviewer.mvp.presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.onlinephotoviewer.app.MyApplication;
import com.example.onlinephotoviewer.app.PhotoViewerApi;
import com.example.onlinephotoviewer.mvp.models.ApiCommentOut;
import com.example.onlinephotoviewer.mvp.models.ApiImageOut;
import com.example.onlinephotoviewer.mvp.models.response.ApiResponseError;
import com.example.onlinephotoviewer.mvp.models.response.ApiResponseSuccess;
import com.example.onlinephotoviewer.mvp.views.PhotosView;
import com.example.onlinephotoviewer.utils.ErrorResponseCreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Andrei on 30.03.2018.
 */

@InjectViewState
public class PhotosPresenter extends MvpPresenter<PhotosView> {

    private final static String LABEL_DEBUG = "Photos debug";
    private final static String DEBUG_GET_IMAGES = "onFailure getImages()";
    private final static String DEBUG_GET_COMMENTS = "onFailure getComments()";
    private final static String DEBUG_DELETE_IMAGE = "onFailure deleteImage()";
    private final static String EXCEPTION_DELETE_IMAGE = "Exception while deleting comments";
    private final static String EXCEPTION_DELETE_COMMENT = "Exception on delete comment query";

    public List<ApiImageOut> getData() {
        return data;
    }

    private List<ApiImageOut> data = new ArrayList<>();


    public PhotosPresenter() {

    }

    public void loadPhotosFromServer(int page) {
        PhotoViewerApi service = MyApplication.getRetrofit().create(PhotoViewerApi.class);
        Call<ApiResponseSuccess<List<ApiImageOut>>> call = service.getImages(MyApplication.getUserInfo().getToken(), page);
        call.enqueue(new Callback<ApiResponseSuccess<List<ApiImageOut>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseSuccess<List<ApiImageOut>>> call,
                                   @NonNull Response<ApiResponseSuccess<List<ApiImageOut>>> response) {
                if (response.isSuccessful()) {
                    ApiResponseSuccess<List<ApiImageOut>> out = response.body();
                    getViewState().refreshPhotosOnLoad(out.data);
                    data = out.data;
                    deleteImagesFromDatabase();
                    getViewState().onSuccessQuery();
                } else {
                    ApiResponseError errorResponse = ErrorResponseCreator.create(response);
                    if (errorResponse != null) {
                        getViewState().onErrorResponse(errorResponse.getError());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseSuccess<List<ApiImageOut>>> call, @NonNull Throwable t) {
                getViewState().onFailedQuery();
                Log.d(LABEL_DEBUG, DEBUG_GET_IMAGES);
            }
        });
    }

    public void deletePhotoWithComments(final ApiImageOut apiImage) {
        final PhotoViewerApi service = MyApplication.getRetrofit().create(PhotoViewerApi.class);
        service.getComments(MyApplication.getUserInfo().getToken(),
                apiImage.getId(),
                0).enqueue(new Callback<ApiResponseSuccess<List<ApiCommentOut>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseSuccess<List<ApiCommentOut>>> call,
                                   @NonNull Response<ApiResponseSuccess<List<ApiCommentOut>>> response) {
                if (response.isSuccessful()) {
                    List<ApiCommentOut> comments = response.body().data;
                    if (comments.size() != 0) {
                        getViewState().onHasConnectedComments();
                        try {
                            if (!deleteComments(service, apiImage, comments)) {
                                return;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.e(LABEL_DEBUG, EXCEPTION_DELETE_IMAGE);
                            return;
                        }
                    }

                    deletePhoto(service, apiImage);
                } else {
                    ApiResponseError errorResponse = ErrorResponseCreator.create(response);
                    if (errorResponse != null)
                        getViewState().onErrorResponse(errorResponse.getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseSuccess<List<ApiCommentOut>>> call,
                                  @NonNull Throwable t) {
                getViewState().onFailedQuery();
                Log.d(LABEL_DEBUG, DEBUG_GET_COMMENTS);
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
            Log.e(LABEL_DEBUG, "Error in future.get()");
        }
        executor.shutdown();
        return deletedAll;
    }

    private boolean deleteComment(PhotoViewerApi service,
                               ApiImageOut apiImage,
                               ApiCommentOut apiComment) {

        Call<ApiResponseSuccess<ApiCommentOut>> call =
                service.deleteComment(MyApplication.getUserInfo().getToken(),
                        apiComment.getId(),
                        apiImage.getId());
        Response<ApiResponseSuccess<ApiCommentOut>> response;
        try {
            response = call.execute();
            if (response.isSuccessful())
                return true;
            ApiResponseError errorResponse = ErrorResponseCreator.create(response);
            if (errorResponse != null)
                getViewState().onErrorResponse(errorResponse.getError());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LABEL_DEBUG, EXCEPTION_DELETE_COMMENT);
        }

        return false;
    }

    private void deletePhoto(final PhotoViewerApi service,
                             final ApiImageOut apiImage) {

        service.deleteImage(MyApplication.getUserInfo().getToken(),
                apiImage.getId()).enqueue(new Callback<ApiResponseSuccess<ApiImageOut>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseSuccess<ApiImageOut>> call,
                                   @NonNull Response<ApiResponseSuccess<ApiImageOut>> response) {

                if (response.isSuccessful()) {
                    getViewState().onSuccessQuery();
                    getViewState().refreshPhotosOnDelete(apiImage);
                } else {
                    ApiResponseError errorResponse = ErrorResponseCreator.create(response);
                    if (errorResponse != null)
                        getViewState().onErrorResponse(errorResponse.getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseSuccess<ApiImageOut>> call,
                                  @NonNull Throwable t) {
                getViewState().onFailedQuery();
                Log.d(LABEL_DEBUG, DEBUG_DELETE_IMAGE);
            }
        });
    }

    public void loadImagesFromDatabase(final int page, final int PER_PAGE) {
        final Realm realm = Realm.getDefaultInstance();
        getViewState().startLoadingData();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm bgRealm) {
                int start = page*PER_PAGE;
                int end = start + PER_PAGE;
                RealmResults<ApiImageOut> apiImages = bgRealm.where(ApiImageOut.class)
                        .findAll()
                        .sort("id", Sort.DESCENDING);

                if (end > apiImages.size()) {
                    data = bgRealm.copyFromRealm(apiImages).subList(start, apiImages.size());
                } else {
                    data = bgRealm.copyFromRealm(apiImages).subList(start, end);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                getViewState().refreshPhotosOnLoad(data);
                getViewState().finishLoadingData();
                realm.close();
            }
        });
    }

    public void insertImagesIntoDatabase(List<ApiImageOut> data) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(data);
        realm.commitTransaction();
    }

    private void deleteImagesFromDatabase() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ApiImageOut.class);
        realm.commitTransaction();
    }
}
