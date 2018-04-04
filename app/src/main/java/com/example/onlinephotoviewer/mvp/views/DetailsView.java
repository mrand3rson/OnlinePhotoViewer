package com.example.onlinephotoviewer.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.onlinephotoviewer.mvp.models.ApiCommentOut;

import java.util.List;

/**
 * Created by Andrei on 31.03.2018.
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface DetailsView extends MvpView {
    void startLoadingComments();
    void finishLoadingComments();

    void onFailedQuery();
    void onSuccessQuery();

    void onErrorResponse(String message);

    void addComment();
    void viewComments(List<ApiCommentOut> comments);
    void refreshCommentsOnAdd(ApiCommentOut comments);
    void refreshCommentsOnDelete(ApiCommentOut comments);
    void deleteComment(ApiCommentOut apiComment);
}
