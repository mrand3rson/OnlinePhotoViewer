package com.example.onlinephotoviewer.mvp.presenters;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.onlinephotoviewer.app.MyApplication;
import com.example.onlinephotoviewer.app.PhotoViewerApi;
import com.example.onlinephotoviewer.mvp.models.ApiCommentIn;
import com.example.onlinephotoviewer.mvp.models.ApiCommentOut;
import com.example.onlinephotoviewer.mvp.models.ApiImageOut;
import com.example.onlinephotoviewer.mvp.models.ApiResponse;
import com.example.onlinephotoviewer.mvp.views.DetailsView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Andrei on 31.03.2018.
 */

@InjectViewState
public class DetailsPresenter extends MvpPresenter<DetailsView> {

    public void setApiImage(ApiImageOut apiImage) {
        this.apiImage = apiImage;
    }

    private ApiImageOut apiImage;


    public void getComments() {
        getViewState().startLoadingComments();

        PhotoViewerApi service = MyApplication.getRetrofit().create(PhotoViewerApi.class);
        Call<ApiResponse<List<ApiCommentOut>>> call = service.getComments(MyApplication.getUserInfo().getToken(),
                apiImage.getId(),
                0);
        call.enqueue(new Callback<ApiResponse<List<ApiCommentOut>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<ApiCommentOut>>> call,
                                   @NonNull Response<ApiResponse<List<ApiCommentOut>>> response) {

                if (response.errorBody() == null) {
                    getViewState().viewComments(response.body().data);
                } else {
                    getViewState().onFailedQuery(response.code() + response.message());
                }
                getViewState().finishLoadingComments();
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<ApiCommentOut>>> call,
                                  @NonNull Throwable t) {
                getViewState().onFailedQuery("Error while connecting to server.");
                getViewState().finishLoadingComments();
            }
        });
    }

    public void sendComment(String commentText) {
        PhotoViewerApi service = MyApplication.getRetrofit().create(PhotoViewerApi.class);
        Call<ApiResponse<ApiCommentOut>> call =
                service.addComment(MyApplication.getUserInfo().getToken(),
                        new ApiCommentIn(commentText),
                        apiImage.getId());
        call.enqueue(new Callback<ApiResponse<ApiCommentOut>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<ApiCommentOut>> call,
                                   @NonNull Response<ApiResponse<ApiCommentOut>> response) {

                if (response.errorBody() == null) {
                    getViewState().onSuccessQuery();

                    ApiCommentOut apiComment = response.body().data;
                    getViewState().refreshCommentsOnAdd(apiComment);
                } else {
                    getViewState().onFailedQuery(response.code() + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<ApiCommentOut>> call,
                                  @NonNull Throwable t) {
                getViewState().onFailedQuery(t.getMessage());
            }
        });
    }

    public void deleteComment(final ApiCommentOut apiComment) {
        PhotoViewerApi service = MyApplication.getRetrofit().create(PhotoViewerApi.class);
        Call<ApiResponse<ApiCommentOut>> call =
                service.deleteComment(MyApplication.getUserInfo().getToken(),
                        apiComment.getId(),
                        apiImage.getId());
        call.enqueue(new Callback<ApiResponse<ApiCommentOut>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<ApiCommentOut>> call,
                                   @NonNull Response<ApiResponse<ApiCommentOut>> response) {

                if (response.errorBody() == null) {
                    getViewState().onSuccessQuery();

                    getViewState().refreshCommentsOnDelete(apiComment);
                } else {
                    getViewState().onFailedQuery(response.code() + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<ApiCommentOut>> call,
                                  @NonNull Throwable t) {
                getViewState().onFailedQuery(t.getMessage());
            }
        });
    }
}
