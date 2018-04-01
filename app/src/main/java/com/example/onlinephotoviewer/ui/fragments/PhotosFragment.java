package com.example.onlinephotoviewer.ui.fragments;


import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.onlinephotoviewer.R;
import com.example.onlinephotoviewer.app.MyApplication;
import com.example.onlinephotoviewer.mvp.models.ApiImageOut;
import com.example.onlinephotoviewer.mvp.presenters.PhotosPresenter;
import com.example.onlinephotoviewer.mvp.views.PhotosView;
import com.example.onlinephotoviewer.ui.adapters.ImagesAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PhotosFragment extends MvpAppCompatFragment implements PhotosView {

    @BindView(R.id.progress_bar)
    ProgressBar mProgress;

    @BindView(R.id.rv)
    RecyclerView mRecycler;

    public PhotosPresenter getPhotosPresenter() {
        return mPhotosPresenter;
    }

    @InjectPresenter
    PhotosPresenter mPhotosPresenter;

    private ImagesAdapter mAdapter;


    public PhotosFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photos, container, false);
        ButterKnife.bind(this, v);
        mProgress.setVisibility(View.GONE);
        viewImages();
        return v;
    }

    @Override
    public void addImage(ApiImageOut apiImage) {
        mPhotosPresenter.insertImageIntoDatabase(apiImage);
        refreshPhotosOnAdd(apiImage);
    }

    @Override
    public void viewImages() {
        if (((MyApplication)getActivity().getApplication()).isOnline()) {
            mPhotosPresenter.loadPhotosFromServer();
        } else {
            mPhotosPresenter.loadImagesFromDatabase();
            Toast.makeText(getActivity(), R.string.warning_mode_offline, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void refreshPhotosOnAdd(ApiImageOut apiImage) {
        ImagesAdapter adapter = (ImagesAdapter) mRecycler.getAdapter();
        adapter.getData().add(0, apiImage);
        adapter.notifyItemInserted(0);
    }

    @Override
    public void refreshPhotosOnDelete(ApiImageOut apiImage) {
        ImagesAdapter adapter = (ImagesAdapter) mRecycler.getAdapter();
        int pos = adapter.getItemPosition(apiImage);
        adapter.getData().remove(pos);
        adapter.notifyItemRemoved(pos);
    }

    @Override
    public void onHasConnectedComments() {
        Toast.makeText(getActivity(), "Cannot delete when has comments", Toast.LENGTH_SHORT).show();
    }

    private void toggleLoading(boolean toggle) {
        if (toggle) {
            mProgress.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        } else {
            mProgress.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void startLoadingData() {
        toggleLoading(true);
    }

    @Override
    public void finishLoadingData() {
        toggleLoading(false);
    }

    @Override
    public void setupAdapter(List<ApiImageOut> data) {
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        mRecycler.setLayoutManager(gridLayoutManager);

        mAdapter = new ImagesAdapter(getActivity(), R.layout.recycler_grid_row, data);
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onSuccessQuery() {
//        Toast.makeText(getActivity(), "SUCCESS LOADING LIST", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailedQuery(String message) {
        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
    }
}
