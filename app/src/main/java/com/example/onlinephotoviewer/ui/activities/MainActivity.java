package com.example.onlinephotoviewer.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.onlinephotoviewer.R;
import com.example.onlinephotoviewer.app.MyApplication;
import com.example.onlinephotoviewer.mvp.models.ApiImageOut;
import com.example.onlinephotoviewer.mvp.presenters.MainPresenter;
import com.example.onlinephotoviewer.mvp.views.MainView;
import com.example.onlinephotoviewer.ui.fragments.MapFragment;
import com.example.onlinephotoviewer.ui.fragments.PhotosFragment;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends MvpAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainView {

    private final int TAKE_PHOTO = 0;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectPresenter
    MainPresenter mMainPresenter;

    private Location mLocation;
    private PhotosFragment mPhotosFragment;

    private MapFragment mMapFragment;


    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();


        TextView mUserName = mNavigationView.getHeaderView(0).findViewById(R.id.user_name);
        mUserName.setText(String.format("Logged as: %s", MyApplication.getUserInfo().getLogin()));

        mNavigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(mNavigationView.getMenu().getItem(0));

        if (!((MyApplication)getApplication()).isOnline())
            Toast.makeText(this, R.string.warning_mode_offline, Toast.LENGTH_SHORT).show();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        boolean select = true;
        int id = item.getItemId();
        if (id == R.id.nav_photos) {
            mPhotosFragment = new PhotosFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, mPhotosFragment)
                    .commit();
        } else if (id == R.id.nav_map) {
            if (((MyApplication)getApplication()).isOnline()) {
                mMapFragment = MapFragment.newInstance(mPhotosFragment.getPhotosPresenter().getData());

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_main, mMapFragment)
                        .commit();
            } else {
                select = false;
                Toast.makeText(this, R.string.warning_mode_offline, Toast.LENGTH_SHORT).show();
            }
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return select;
    }

    @OnClick(R.id.fab)
    public void takePhoto() {
        if (((MyApplication)getApplication()).isOnline()) {
            listenLocation();

            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            Uri fileUri = mMainPresenter.getOutputMediaFileUri();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, TAKE_PHOTO);
        } else {
            Toast.makeText(this, R.string.warning_mode_offline, Toast.LENGTH_SHORT).show();
        }
    }

    private void listenLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };
        if (lm != null) {
            lm.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO) {
            if (resultCode != 0) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (mLocation != null)
                    mMainPresenter.uploadImage(mLocation);
                else {
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (location != null) {
                        mMainPresenter.uploadImage(location);
                    }
                    else {
                        Toast.makeText(this, "Cannot get location", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void deleteTempPhoto(File file) {
        if (!file.delete()) {
            Toast.makeText(this, "Temp file is not deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccessUploading(ApiImageOut apiImage) {
//        Toast.makeText(this, "SUCCESS uploading", Toast.LENGTH_SHORT).show();
        mPhotosFragment.addImage(apiImage);
    }

    @Override
    public void onFailedUploading(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void deletePhoto(ApiImageOut apiImageOut) {
        if (((MyApplication)getApplication()).isOnline()) {
            mPhotosFragment.getPhotosPresenter().checkCommentsAndDeletePhoto(apiImageOut);
        } else {
            Toast.makeText(this,
                    getString(R.string.warning_offline),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
