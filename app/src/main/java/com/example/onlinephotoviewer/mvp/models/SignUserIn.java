package com.example.onlinephotoviewer.mvp.models;

/**
 * Created by Andrei on 29.03.2018.
 */

public class SignUserIn {
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    String login;
    String password;

    public SignUserIn(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
