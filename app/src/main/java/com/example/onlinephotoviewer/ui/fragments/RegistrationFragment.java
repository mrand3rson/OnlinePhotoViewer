package com.example.onlinephotoviewer.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.onlinephotoviewer.R;
import com.example.onlinephotoviewer.app.MyApplication;
import com.example.onlinephotoviewer.mvp.models.response.ApiError;
import com.example.onlinephotoviewer.mvp.presenters.LoginPresenter;
import com.example.onlinephotoviewer.mvp.presenters.RegistrationPresenter;
import com.example.onlinephotoviewer.mvp.views.RegistrationView;
import com.example.onlinephotoviewer.ui.activities.MainActivity;
import com.example.onlinephotoviewer.ui.activities.SignActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RegistrationFragment extends MvpAppCompatFragment implements RegistrationView {

    private final static String field_login = "login";
    private final static String field_password = "password";

    @BindView(R.id.progress_bar)
    ProgressBar mProgressView;

    @BindView(R.id.main_layout)
    View mLoginFormView;

    @BindView(R.id.login)
    EditText mLoginView;

    @BindView(R.id.password)
    EditText mPasswordView;

    @BindView(R.id.password_confirmation)
    EditText mPasswordConfirmationView;

    @InjectPresenter
    RegistrationPresenter mRegistrationPresenter;


    public RegistrationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registration, container, false);
        ButterKnife.bind(this, v);
        mProgressView.setVisibility(View.GONE);
        return v;
    }

    @OnClick(R.id.button_ok)
    public void attemptRegistration() {
        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConfirmation = mPasswordConfirmationView.getText().toString();

        mRegistrationPresenter.signUp(login, password, passwordConfirmation);
    }

    private void toggleProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void startSignUp() {
        toggleProgress(true);
    }

    @Override
    public void finishSignUp() {
        toggleProgress(false);
    }

    @Override
    public void failedSignUp(ArrayList<ApiError> errors) {
        String loginError = null;
        String passwordError = null;
        for (ApiError e : errors) {
            switch (e.getField()) {
                case field_login: {
                    loginError = e.getMessage();
                    break;
                }
                case field_password: {
                    passwordError = e.getMessage();
                    break;
                }
            }
        }

        showFormError(loginError, passwordError);
    }

    @Override
    public void failedSignUp(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void failedQuery(String message) {
        if (!((MyApplication)getActivity().getApplication()).isOnline()) {
            Toast.makeText(getActivity(), R.string.warning_offline, Toast.LENGTH_SHORT).show();
        }
        Log.d(LoginPresenter.getLabelDebug(), message);
    }

    @Override
    public void successSignUp() {
        final Intent intent = new Intent(getActivity(), MainActivity.class);
        ((SignActivity)getActivity()).getSignPresenter().saveLastSession(MyApplication.getUserInfo());
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void hideFormError() {
        mLoginView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmationView.setError(null);
    }

    @Override
    public void showFormError(String loginError, String passwordError) {
        mLoginView.setError(loginError);
        mPasswordView.setError(passwordError);
    }

    @Override
    public void showFormError(Integer loginError, Integer passwordError, Integer passwordConfirmationError) {
        mLoginView.setError(loginError == null? null: getString(loginError));
        mPasswordView.setError(passwordError == null? null: getString(passwordError));
        mPasswordConfirmationView.setError(passwordConfirmationError == null? null:
                getString(passwordConfirmationError));
    }
}
