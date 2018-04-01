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

    private final int ITEMS_PER_PAGE = 20;

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

        setupRecycler();
        viewImages(0);
        return v;
    }

    private void setupRecycler() {
        final RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecycler.setLayoutManager(gridLayoutManager);
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {

                super.onScrollStateChanged(recyclerView, newState);

                if (!mRecycler.canScrollVertically(1)) {
                    int unfinishedPage = (mAdapter.getItemCount() % ITEMS_PER_PAGE > 0? 1: 0);
                    int newPage = mAdapter.getItemCount() / ITEMS_PER_PAGE + unfinishedPage;
                    if (mAdapter.getItemCount() >= newPage *ITEMS_PER_PAGE) {
                        PhotosFragment.this.viewImages(newPage);
                    }
                }
            }
        });
    }

    @Override
    public void addImage(ApiImageOut apiImage) {
        refreshPhotosOnAdd(apiImage);
    }

    @Override
    public void viewImages(int page) {
        if (((MyApplication)getActivity().getApplication()).isOnline()) {
            mPhotosPresenter.loadPhotosFromServer(page);
        } else {
            mPhotosPresenter.loadImagesFromDatabase(page, ITEMS_PER_PAGE);
        }
    }

    @Override
    public void refreshPhotosOnLoad(List<ApiImageOut> data) {
        if (mAdapter == null) {
            mAdapter = new ImagesAdapter(getActivity(), R.layout.recycler_grid_row, data);
            mRecycler.setAdapter(mAdapter);
        } else {
            int offset = mAdapter.getItemCount() % ITEMS_PER_PAGE;
            List<ApiImageOut> filteredData = data.subList(offset, data.size());
            mAdapter.getData().addAll(filteredData);
            mAdapter.notifyItemRangeInserted(mAdapter.getItemCount(), data.size()-offset);
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
        Toast.makeText(getActivity(), "Deleting comments...", Toast.LENGTH_SHORT).show();
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
    public void onSuccessQuery() {

    }

    @Override
    public void onFailedQuery(String message) {
        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        mPhotosPresenter.insertImagesIntoDatabase(mAdapter.getData());
        super.onStop();
    }
}
