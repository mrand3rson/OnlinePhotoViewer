package com.example.onlinephotoviewer.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.onlinephotoviewer.R;
import com.example.onlinephotoviewer.mvp.presenters.RegistrationPresenter;
import com.example.onlinephotoviewer.mvp.views.RegistrationView;
import com.example.onlinephotoviewer.ui.activities.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RegistrationFragment extends MvpAppCompatFragment implements RegistrationView {

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
    public void failedSignUp(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideFormError() {
        mLoginView.setError(null);
        mPasswordView.setError(null);
    }

    @Override
    public void showFormError(Integer emailError, Integer passwordError) {
        mLoginView.setError(emailError == -1 ? null : getActivity().getString(emailError));
        mPasswordView.setError(passwordError == -1 ? null : getActivity().getString(passwordError));
    }

    @Override
    public void successSignUp() {
        final Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
