package com.example.onlinephotoviewer.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.onlinephotoviewer.mvp.models.SignUserOut;
import com.example.onlinephotoviewer.mvp.views.SignView;

import io.realm.Realm;

/**
 * Created by Andrei on 31.03.2018.
 */

@InjectViewState
public class SignPresenter extends MvpPresenter<SignView> {

    public SignPresenter() {

    }

    public SignUserOut offlineAuthentication() {
        SignUserOut user;

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        user = realm.where(SignUserOut.class).findFirst();
        realm.commitTransaction();

        return user;
    }

    public void saveLastSession(final SignUserOut user) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SignUserOut.class);
        realm.copyToRealm(user);
        realm.commitTransaction();
    }
}
