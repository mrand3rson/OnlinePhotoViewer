package com.example.onlinephotoviewer.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.onlinephotoviewer.mvp.models.response.ApiError;

import java.util.ArrayList;

/**
 * Created by Andrei on 30.03.2018.
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface RegistrationView extends MvpView {
    void startSignUp();
    void finishSignUp();

    void failedQuery(String message);

    void hideFormError();
    void showFormError(String loginError, String passwordError);
    void showFormError(Integer loginError, Integer passwordError, Integer passwordConfirmationError);

    void failedSignUp(ArrayList<ApiError> errors);
    void failedSignUp(String message);

    @StateStrategyType(SkipStrategy.class)
    void successSignUp();
}
