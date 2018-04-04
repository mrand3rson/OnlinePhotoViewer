package com.example.onlinephotoviewer.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.onlinephotoviewer.mvp.models.ApiImageOut;

import java.util.List;

/**
 * Created by Andrei on 30.03.2018.
 */

@StateStrategyType(SingleStateStrategy.class)
public interface PhotosView extends MvpView {
    void startLoadingData();
    void finishLoadingData();

    void onSuccessQuery();
    void onFailedQuery();
    void onErrorResponse(String message);

    void viewImages(int page);
    void addImage(ApiImageOut apiImage);

    void refreshPhotosOnLoad(List<ApiImageOut> data);
    void refreshPhotosOnAdd(ApiImageOut apiImage);
    void refreshPhotosOnDelete(ApiImageOut apiImage);
    void onHasConnectedComments();
}
