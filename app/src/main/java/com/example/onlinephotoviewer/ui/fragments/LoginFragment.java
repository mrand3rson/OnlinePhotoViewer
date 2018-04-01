package com.example.onlinephotoviewer.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.onlinephotoviewer.R;
import com.example.onlinephotoviewer.mvp.presenters.LoginPresenter;
import com.example.onlinephotoviewer.mvp.views.LoginView;
import com.example.onlinephotoviewer.ui.activities.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends MvpAppCompatFragment implements LoginView {

    @BindView(R.id.progress_bar)
    ProgressBar mProgressView;

    @BindView(R.id.main_layout)
    View mLoginFormView;

    @BindView(R.id.login)
    EditText mLoginView;

    @BindView(R.id.password)
    EditText mPasswordView;

    @InjectPresenter
    LoginPresenter mLoginPresenter;

    public LoginFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, v);
        mProgressView.setVisibility(View.GONE);
        return v;
    }


    @OnClick(R.id.button_ok)
    public void attemptSignIn() {
        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        mLoginPresenter.signIn(login, password);
    }

    private void toggleProgress(final boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void startSignIn() {
        toggleProgress(true);
    }

    @Override
    public void finishSignIn() {
        toggleProgress(false);
    }

    @Override
    public void failedSignIn(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideFormError() {
        mLoginView.setError(null);
        mPasswordView.setError(null);
    }

    @Override
    public void showFormError(Integer emailError, Integer passwordError) {
        mLoginView.setError(emailError == null ? null : getString(emailError));
        mPasswordView.setError(passwordError == null ? null : getString(passwordError));
    }

    @Override
    public void successSignIn() {
        final Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
