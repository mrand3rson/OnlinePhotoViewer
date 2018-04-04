package com.example.onlinephotoviewer.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.onlinephotoviewer.mvp.models.ApiImageOut;

/**
 * Created by Andrei on 30.03.2018.
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface MainView extends MvpView {
    void onSuccessUploading(ApiImageOut apiImage);
    void onFailedUploading(String message);
    void onErrorResponse(String message);
}
