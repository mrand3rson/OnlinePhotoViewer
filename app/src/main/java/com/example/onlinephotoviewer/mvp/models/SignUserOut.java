package com.example.onlinephotoviewer.mvp.models;

import io.realm.RealmObject;

/**
 * Created by Andrei on 29.03.2018.
 */

public class SignUserOut extends RealmObject {

    public String getLogin() {
        return login;
    }

    public String getToken() {
        return token;
    }

    public int getUserId() {
        return userId;
    }

    private String login;
    private String token;
    private int userId;
}
