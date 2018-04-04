package com.example.onlinephotoviewer.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.onlinephotoviewer.R;
import com.example.onlinephotoviewer.app.MyApplication;
import com.example.onlinephotoviewer.mvp.models.ApiCommentOut;
import com.example.onlinephotoviewer.mvp.models.ApiImageOut;
import com.example.onlinephotoviewer.mvp.presenters.DetailsPresenter;
import com.example.onlinephotoviewer.mvp.views.DetailsView;
import com.example.onlinephotoviewer.ui.adapters.CommentsAdapter;
import com.example.onlinephotoviewer.utils.DateFormatter;
import com.example.onlinephotoviewer.utils.VerticalSpaceItemDecoration;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsActivity extends MvpAppCompatActivity implements DetailsView {

    public ApiImageOut getApiImage() {
        return mApiImage;
    }

    public void setApiImage(ApiImageOut mApiImage) {
        this.mApiImage = mApiImage;
    }

    private ApiImageOut mApiImage;

    @BindDimen(R.dimen.comment_padding)
    int mCommentPadding;

    @BindDimen(R.dimen.comment_spacing)
    int mCommentSpacing;

    @BindView(R.id.imageView2)
    ImageView mImage;

    @BindView(R.id.textView2)
    TextView mDate;

    @BindView(R.id.progress_bar)
    ProgressBar mProgress;

    @BindView(R.id.comments_view)
    RecyclerView mCommentsView;

    @BindView(R.id.edit_comment)
    EditText mEditComment;

    @InjectPresenter
    DetailsPresenter mDetailsPresenter;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            mApiImage = intent.getParcelableExtra("api_image");
            mDetailsPresenter.setApiImage(mApiImage);
            byte[] bytes = intent.getByteArrayExtra("image");
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            mImage.setImageBitmap(bmp);
        }

        mCommentsView.addItemDecoration(new VerticalSpaceItemDecoration(mCommentSpacing));
        mCommentsView.setLayoutManager(new LinearLayoutManager(this));

        mDate.setText(DateFormatter.formatDate(
                DateFormatter.timestampToDate(mApiImage.getDate())));

        if (((MyApplication)getApplication()).isOnline()) {
            mDetailsPresenter.getComments();
        }
    }

    private void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @OnClick(R.id.send_comment)
    public void addComment() {
        hideSoftKeyboard(getCurrentFocus());

        if (((MyApplication)getApplication()).isOnline()) {
            String commentText = mEditComment.getText().toString();
            mEditComment.getText().clear();
            mDetailsPresenter.sendComment(commentText);
        } else {
            Toast.makeText(this, R.string.warning_offline, Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleLoading(boolean toggle) {
        if (toggle) {
            mProgress.setVisibility(View.VISIBLE);
            mCommentsView.setVisibility(View.INVISIBLE);
        } else {
            mProgress.setVisibility(View.INVISIBLE);
            mCommentsView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void startLoadingComments() {
        toggleLoading(true);
    }

    @Override
    public void finishLoadingComments() {
        toggleLoading(false);
    }

    @Override
    public void onFailedQuery() {
        if (!((MyApplication)getApplication()).isOnline()) {
            Toast.makeText(this, R.string.warning_offline, Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, R.string.warning_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorResponse(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessQuery() {

    }

    @Override
    public void viewComments(List<ApiCommentOut> comments) {
        if (mCommentsView.getAdapter() == null) {
            mCommentsView.setAdapter(new CommentsAdapter(this,
                    R.layout.recycler_comments_row,
                    comments));
        } else {
            mCommentsView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void refreshCommentsOnAdd(ApiCommentOut comment) {
        CommentsAdapter adapter = (CommentsAdapter) mCommentsView.getAdapter();
        adapter.getData().add(comment);
        adapter.notifyItemInserted(adapter.getItemCount());
    }

    @Override
    public void refreshCommentsOnDelete(ApiCommentOut comment) {
        CommentsAdapter adapter = (CommentsAdapter) mCommentsView.getAdapter();
        int position = adapter.getItemPosition(comment);
        adapter.getData().remove(position);
        adapter.notifyItemRemoved(position);
    }


    @Override
    public void deleteComment(ApiCommentOut apiComment) {
        mDetailsPresenter.deleteComment(apiComment);
    }
}
