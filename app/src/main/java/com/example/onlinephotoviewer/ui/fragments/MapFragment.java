package com.example.onlinephotoviewer.ui.fragments;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.onlinephotoviewer.R;
import com.example.onlinephotoviewer.mvp.models.ApiImageOut;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    @BindView(R.id.map)
    MapView mapView;

    private GoogleMap map;
    private List<ApiImageOut> mImages;

    public MapFragment() {

    }

    public static MapFragment newInstance(List<ApiImageOut> images) {
        MapFragment fragment = new MapFragment();
        fragment.setData(images);
        return fragment;
    }

    private void setData(List<ApiImageOut> images) {
        this.mImages = images;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        addMapMarkers();
    }

    private void addMapMarkers() {
        if (mImages != null && mImages.size() > 0) {
            for (final ApiImageOut apiImage : mImages) {
                final LatLng coords = new LatLng(apiImage.getLat(), apiImage.getLng());

                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Bitmap smallBitmap = ThumbnailUtils.extractThumbnail(bitmap,
                                80, 80);
                        map.addMarker(new MarkerOptions()
                                .position(coords)
                                .icon(BitmapDescriptorFactory.fromBitmap(smallBitmap)));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {}

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {}
                };
                Picasso.with(getActivity())
                        .load(apiImage.getUrl())
                        .into(target);
            }
        }
    }
}
