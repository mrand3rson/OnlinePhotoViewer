package com.example.onlinephotoviewer.mvp.presenters;

import android.support.annotation.NonNull;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.onlinephotoviewer.app.MyApplication;
import com.example.onlinephotoviewer.app.PhotoViewerApi;
import com.example.onlinephotoviewer.mvp.models.response.ApiResponseErrorAuth;
import com.example.onlinephotoviewer.mvp.models.response.ApiResponseSuccess;
import com.example.onlinephotoviewer.mvp.models.SignUserOut;
import com.example.onlinephotoviewer.mvp.models.SignUserIn;
import com.example.onlinephotoviewer.mvp.views.LoginView;
import com.example.onlinephotoviewer.utils.ErrorResponseCreator;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.onlinephotoviewer.app.MyApplication.getRetrofit;
import static com.example.onlinephotoviewer.utils.ErrorResponseCreator.LOG_STATUS;

/**
 * Created by Andrei on 29.03.2018.
 */

@InjectViewState
public class LoginPresenter extends MvpPresenter<LoginView> {

    public static String getLabelDebug() {
        return LABEL_DEBUG;
    }

    private static final String LABEL_DEBUG = "Login debug";


    public LoginPresenter() {

    }

    public void signIn(String login, String password) {

        getViewState().hideFormError();
        getViewState().startSignIn();

        SignUserIn user = new SignUserIn(login, password);
        PhotoViewerApi service = getRetrofit().create(PhotoViewerApi.class);
        Call<ApiResponseSuccess<SignUserOut>> call = service.signIn(user);
        call.enqueue(new Callback<ApiResponseSuccess<SignUserOut>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponseSuccess<SignUserOut>> call,
                                   @NonNull Response<ApiResponseSuccess<SignUserOut>> response) {
                getViewState().finishSignIn();

                if (response.isSuccessful()) {
                    ApiResponseSuccess<SignUserOut> apiResponse = response.body();
                    MyApplication.setUserInfo(apiResponse.data);
                    getViewState().successSignIn();
                } else {
                    ApiResponseErrorAuth errorResponse =
                            ErrorResponseCreator.createForAuth(response, LABEL_DEBUG);
                    Log.d(LABEL_DEBUG, String.format(Locale.getDefault(),
                            LOG_STATUS,
                            errorResponse.getStatus(),
                            errorResponse.getError()));

                    if (errorResponse.getErrorsList() != null)
                        getViewState().failedSignIn(errorResponse.getErrorsList());
                    else
                        getViewState().failedSignIn(errorResponse.getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponseSuccess<SignUserOut>> call, @NonNull Throwable t) {
                Log.d(LABEL_DEBUG, t.getMessage());
                getViewState().failedQuery();
                getViewState().finishSignIn();
            }
        });
    }
}
