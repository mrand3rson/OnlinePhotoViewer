package com.example.onlinephotoviewer.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.onlinephotoviewer.mvp.models.response.ApiError;

import java.util.ArrayList;

/**
 * Created by Andrei on 29.03.2018.
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface LoginView extends MvpView {
    void startSignIn();
    void finishSignIn();

    void failedQuery();

    void hideFormError();
    void showFormError(String loginError, String passwordError);

    void failedSignIn(ArrayList<ApiError> errors);
    void failedSignIn(String message);

    @StateStrategyType(SkipStrategy.class)
    void successSignIn();
}
