package com.example.onlinephotoviewer.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.onlinephotoviewer.R;
import com.example.onlinephotoviewer.app.MyApplication;
import com.example.onlinephotoviewer.mvp.models.SignUserOut;
import com.example.onlinephotoviewer.mvp.presenters.SignPresenter;
import com.example.onlinephotoviewer.mvp.views.SignView;
import com.example.onlinephotoviewer.ui.fragments.LoginFragment;
import com.example.onlinephotoviewer.ui.fragments.RegistrationFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignActivity extends MvpAppCompatActivity implements SignView {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.container)
    ViewPager mViewPager;

    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    @InjectPresenter
    SignPresenter mSignPresenter;

    private LoginFragment mLoginFragment = new LoginFragment();
    private RegistrationFragment mRegistrationFragment = new RegistrationFragment();

    private SectionsPagerAdapter mSectionsPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApplication.setUserInfo(null);
        if (!((MyApplication)getApplication()).isOnline()) {
            SignUserOut userInfo = mSignPresenter.offlineAuthentication();
            if (userInfo != null) {
                MyApplication.setUserInfo(userInfo);
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }


    class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mLoginFragment;
                case 1:
                    return mRegistrationFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
