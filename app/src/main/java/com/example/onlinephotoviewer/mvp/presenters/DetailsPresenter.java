package com.example.onlinephotoviewer.mvp.presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.onlinephotoviewer.app.MyApplication;
import com.example.onlinephotoviewer.app.PhotoViewerApi;
import com.example.onlinephotoviewer.mvp.models.ApiCommentIn;
import com.example.onlinephotoviewer.mvp.models.ApiCommentOut;
import com.example.onlinephotoviewer.mvp.models.ApiImageOut;
import com.example.onlinephotoviewer.mvp.models.response.ApiResponseError;
import com.example.onlinephotoviewer.mvp.models.response.ApiResponseSuccess;
import com.example.onlinephotoviewer.mvp.views.DetailsView;
import com.example.onlinephotoviewer.utils.ErrorResponseCreator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Andrei on 31.03.2018.
 */

@InjectViewState
public class DetailsPresenter extends MvpPresenter<DetailsView> {

    private final static String LABEL_DEBUG = "Details debug";

    public void setApiImage(ApiImageOut apiImage) {
        this.apiImage = apiImage;
    }

    private ApiImageOut apiImage;


    public void getComments() {
        getViewState().startLoadingComments();

        PhotoViewerApi service = MyApplication.getRetrofit().create(PhotoViewerApi.class);
        Call<ApiResponseSuccess<List<ApiCommentOut>>> call = service.getComments(MyApplication.getUserInfo().getToken(),
                apiImage.getId(),
                0);
        call.enqueue(new Callback<ApiResponseSuccess<List<ApiCommentOut>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseSuccess<List<ApiCommentOut>>> call,
                                   @NonNull Response<ApiResponseSuccess<List<ApiCommentOut>>> response) {

                if (response.isSuccessful()) {
                    List<ApiCommentOut> comments = response.body().data;
                    getViewState().viewComments(comments);
                } else {
                    ApiResponseError errorResponse = ErrorResponseCreator.create(response);
                    if (errorResponse != null) {
                        getViewState().onErrorResponse(errorResponse.getError());
                    }
                }
                getViewState().finishLoadingComments();
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseSuccess<List<ApiCommentOut>>> call,
                                  @NonNull Throwable t) {
                Log.d(LABEL_DEBUG, t.getMessage());
                getViewState().onFailedQuery();
                getViewState().finishLoadingComments();
            }
        });
    }

    public void sendComment(String commentText) {
        PhotoViewerApi service = MyApplication.getRetrofit().create(PhotoViewerApi.class);
        Call<ApiResponseSuccess<ApiCommentOut>> call =
                service.addComment(MyApplication.getUserInfo().getToken(),
                        new ApiCommentIn(commentText),
                        apiImage.getId());
        call.enqueue(new Callback<ApiResponseSuccess<ApiCommentOut>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseSuccess<ApiCommentOut>> call,
                                   @NonNull Response<ApiResponseSuccess<ApiCommentOut>> response) {

                if (response.isSuccessful()) {
                    getViewState().onSuccessQuery();

                    ApiCommentOut apiComment = response.body().data;
                    getViewState().refreshCommentsOnAdd(apiComment);
                } else {
                    ApiResponseError errorResponse = ErrorResponseCreator.create(response);
                    if (errorResponse != null) {
                        getViewState().onErrorResponse(errorResponse.getError());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseSuccess<ApiCommentOut>> call,
                                  @NonNull Throwable t) {
                Log.d(LABEL_DEBUG, t.getMessage());
                getViewState().onFailedQuery();
            }
        });
    }

    public void deleteComment(final ApiCommentOut apiComment) {
        PhotoViewerApi service = MyApplication.getRetrofit().create(PhotoViewerApi.class);
        Call<ApiResponseSuccess<ApiCommentOut>> call =
                service.deleteComment(MyApplication.getUserInfo().getToken(),
                        apiComment.getId(),
                        apiImage.getId());
        call.enqueue(new Callback<ApiResponseSuccess<ApiCommentOut>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseSuccess<ApiCommentOut>> call,
                                   @NonNull Response<ApiResponseSuccess<ApiCommentOut>> response) {

                if (response.isSuccessful()) {
                    getViewState().onSuccessQuery();
                    getViewState().refreshCommentsOnDelete(apiComment);
                } else {
                    ApiResponseError errorResponse = ErrorResponseCreator.create(response);
                    if (errorResponse != null) {
                        getViewState().onErrorResponse(errorResponse.getError());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseSuccess<ApiCommentOut>> call,
                                  @NonNull Throwable t) {
                Log.d(LABEL_DEBUG, t.getMessage());
                getViewState().onFailedQuery();
            }
        });
    }
}
