package com.example.onlinephotoviewer.ui.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.example.onlinephotoviewer.app.MyApplication;
import com.example.onlinephotoviewer.mvp.models.ApiImageOut;
import com.example.onlinephotoviewer.ui.activities.MainActivity;
import com.example.onlinephotoviewer.ui.fragments.DetailsActivity;
import com.example.onlinephotoviewer.utils.Base64Formatter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        holder.setIsRecyclable(false);

        if (!((MyApplication)(
                (Activity)context)
                .getApplication()).isOnline()) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Callable<Bitmap> callable = new Callable<Bitmap>() {
                @Override
                public Bitmap call() {
                    String base64 = data.get(holder.getAdapterPosition())
                            .getBase64Image();
                    if (base64 != null)
                        return Base64Formatter.decodeBase64(base64);
                    else
                        return null;
                }
            };
            Future<Bitmap> future = executor.submit(callable);

            try {
                Bitmap bmp = future.get();
                if (bmp != null)
                    holder.iv.setImageBitmap(bmp);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            executor.shutdown();
        } else {
            Picasso.with(context)
                    .load(data.get(position).getUrl())
                    .placeholder(android.R.drawable.arrow_down_float)
                    .priority(position < data.size() / 2 ? Picasso.Priority.HIGH : Picasso.Priority.LOW)
                    .into(holder.target);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
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

        Target target;

        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            ButterKnife.bind(this, cv);
            cv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ViewHolder.this.setIsRecyclable(true);
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                ((MainActivity) context).deletePhoto(
                                                        data.get(getAdapterPosition()));
                                                ViewHolder.this.cv.setOnClickListener(null);
                                        }
                                    })
                            .setNegativeButton(android.R.string.no,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ViewHolder.this.setIsRecyclable(false);
                                            dialogInterface.dismiss();
                                        }
                                    })
                            .setTitle(context.getString(R.string.delete_confirmation))
                            .create()
                            .show();
                    return false;
                }
            });

            target = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    iv.setImageBitmap(bitmap);

                    if (getAdapterPosition() != -1) {
                        if (data.get(getAdapterPosition()).getBase64Image() == null) {
                            final ApiImageOut apiImage = data.get(getAdapterPosition());
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String base64 = Base64Formatter.convertToBase64(bitmap);
                                    apiImage.setBase64Image(base64);
                                }
                            }).start();
                        }
                    }


                    cv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showImageDetails(
                                    data.get(getAdapterPosition()));
                        }
                    });
                }
                @Override
                public void onBitmapFailed(Drawable errorDrawable) {}
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {}

            };
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
