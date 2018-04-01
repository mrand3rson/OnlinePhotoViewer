package com.example.onlinephotoviewer.mvp.presenters;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.onlinephotoviewer.R;
import com.example.onlinephotoviewer.app.MyApplication;
import com.example.onlinephotoviewer.app.PhotoViewerApi;
import com.example.onlinephotoviewer.mvp.models.ApiResponse;
import com.example.onlinephotoviewer.mvp.models.SignUserOut;
import com.example.onlinephotoviewer.mvp.models.SignUserIn;
import com.example.onlinephotoviewer.mvp.views.LoginView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Andrei on 29.03.2018.
 */

@InjectViewState
public class LoginPresenter extends MvpPresenter<LoginView> {

    public LoginPresenter() {

    }

    public void signIn(String login, String password) {
        int emailError = -1;
        int passwordError = -1;

        getViewState().hideFormError();

        if (TextUtils.isEmpty(login)) {
            emailError = R.string.error_field_required;
        }

        if (TextUtils.isEmpty(password)) {
            passwordError = R.string.error_invalid_password;
        }

        if (emailError != -1 || passwordError != -1) {
            getViewState().showFormError(emailError, passwordError);
            return;
        }

        getViewState().startSignIn();

        SignUserIn user = new SignUserIn(login, password);
        PhotoViewerApi service = MyApplication.getRetrofit().create(PhotoViewerApi.class);
        Call<ApiResponse<SignUserOut>> call = service.signIn(user);
        call.enqueue(new Callback<ApiResponse<SignUserOut>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<SignUserOut>> call, @NonNull Response<ApiResponse<SignUserOut>> response) {
                getViewState().finishSignIn();

                if (response.errorBody() == null) {
                    ApiResponse<SignUserOut> apiResponse = response.body();
                    MyApplication.setUserInfo(apiResponse.data);
                    getViewState().successSignIn();
                } else {
                    getViewState().failedSignIn("Incorrect data");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<SignUserOut>> call, @NonNull Throwable t) {
                getViewState().finishSignIn();
                getViewState().failedSignIn(t.getMessage());
            }
        });
    }
}
