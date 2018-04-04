package com.example.onlinephotoviewer.mvp.presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.onlinephotoviewer.R;
import com.example.onlinephotoviewer.app.MyApplication;
import com.example.onlinephotoviewer.app.PhotoViewerApi;
import com.example.onlinephotoviewer.mvp.models.SignUserIn;
import com.example.onlinephotoviewer.mvp.models.SignUserOut;
import com.example.onlinephotoviewer.mvp.models.response.ApiResponseErrorAuth;
import com.example.onlinephotoviewer.mvp.models.response.ApiResponseSuccess;
import com.example.onlinephotoviewer.mvp.views.RegistrationView;
import com.example.onlinephotoviewer.utils.ErrorResponseCreator;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.onlinephotoviewer.utils.ErrorResponseCreator.LOG_STATUS;

/**
 * Created by Andrei on 30.03.2018.
 */

@InjectViewState
public class RegistrationPresenter extends MvpPresenter<RegistrationView> {

    public static String getLabelDebug() {
        return LABEL_DEBUG;
    }

    private static final String LABEL_DEBUG = "Registration debug";


    public RegistrationPresenter() {

    }

    public void signUp(String login, String password, String passwordConfirmation) {

        getViewState().hideFormError();
        getViewState().startSignUp();

        if (!password.equals(passwordConfirmation)) {
            getViewState().finishSignUp();
            getViewState().showFormError(null, null, R.string.error_password_confirmation);
            return;
        }
        SignUserIn user = new SignUserIn(login, password);
        PhotoViewerApi service = MyApplication.getRetrofit().create(PhotoViewerApi.class);
        Call<ApiResponseSuccess<SignUserOut>> call = service.signUp(user);
        call.enqueue(new Callback<ApiResponseSuccess<SignUserOut>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseSuccess<SignUserOut>> call,
                                   @NonNull Response<ApiResponseSuccess<SignUserOut>> response) {
                getViewState().finishSignUp();

                if (response.isSuccessful()) {
                    ApiResponseSuccess<SignUserOut> apiResponse = response.body();
                    MyApplication.setUserInfo(apiResponse.data);
                    getViewState().successSignUp();
                } else {
                    ApiResponseErrorAuth errorResponse =
                            ErrorResponseCreator.createForAuth(response, LABEL_DEBUG);

                    Log.d(LABEL_DEBUG, String.format(Locale.getDefault(),
                            LOG_STATUS,
                            errorResponse.getStatus(),
                            errorResponse.getError()));

                    if (errorResponse.getErrorsList() != null)
                        getViewState().failedSignUp(errorResponse.getErrorsList());
                    else
                        getViewState().failedSignUp(errorResponse.getError());

                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseSuccess<SignUserOut>> call, @NonNull Throwable t) {
                getViewState().finishSignUp();
                getViewState().failedQuery(t.getMessage());
            }
        });
    }
}
