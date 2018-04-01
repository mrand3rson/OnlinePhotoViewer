package com.example.onlinephotoviewer.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by Andrei on 30.03.2018.
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface RegistrationView extends MvpView {
    void startSignUp();

    void finishSignUp();

    void failedSignUp(String message);

    void hideFormError();

    void showFormError(Integer emailError, Integer passwordError);

    @StateStrategyType(SkipStrategy.class)
    void successSignUp();
}
