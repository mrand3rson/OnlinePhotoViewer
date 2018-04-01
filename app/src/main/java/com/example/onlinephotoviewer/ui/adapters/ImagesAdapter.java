package com.example.onlinephotoviewer.ui.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onlinephotoviewer.R;
import com.example.onlinephotoviewer.mvp.models.ApiImageOut;
import com.example.onlinephotoviewer.ui.activities.MainActivity;
import com.example.onlinephotoviewer.ui.fragments.DetailsActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrei on 30.03.2018.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private final Context context;
    private final int resource;
    private final List<ApiImageOut> data;


    public ImagesAdapter(Context context, int resource, List<ApiImageOut> data) {
        this.context = context;
        this.resource = resource;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(resource, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Picasso.with(context)
                .load(data.get(position).getUrl())
                .priority(position < data.size() / 2? Picasso.Priority.HIGH: Picasso.Priority.LOW)
                .into(holder.iv, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.cv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                holder.showImageDetails(
                                        data.get(holder.getAdapterPosition()));
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });
    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public int getItemPosition(ApiImageOut apiImage) {
        return data.indexOf(apiImage);
    }

    public List<ApiImageOut> getData() {
        return data;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_view)
        CardView cv;

        @BindView(R.id.row)
        TextView tv;

        @BindView(R.id.image)
        ImageView iv;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            ButterKnife.bind(this, cv);
            cv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ((MainActivity)context).deletePhoto(
                                                    data.get(getAdapterPosition()));
                                        }
                                    })
                            .setNegativeButton(android.R.string.no,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    })
                            .setTitle(context.getString(R.string.delete_confirmation))
                            .create()
                            .show();
                    return false;
                }
            });
        }

        private void showImageDetails(ApiImageOut apiImageOut) {

            Intent i = new Intent(context, DetailsActivity.class);

            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (Activity) context,
                            Pair.create((View) iv, "imagest"));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ((BitmapDrawable)iv.getDrawable()).getBitmap()
                    .compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytes = stream.toByteArray();
            i.putExtra("api_image", apiImageOut);
            i.putExtra("image", bytes);
            context.startActivity(i, options.toBundle());
        }
    }
}
