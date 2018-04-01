package com.example.onlinephotoviewer.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.onlinephotoviewer.R;
import com.example.onlinephotoviewer.mvp.models.ApiCommentOut;
import com.example.onlinephotoviewer.ui.fragments.DetailsActivity;
import com.example.onlinephotoviewer.utils.DateFormatter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Andrei on 31.03.2018.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private final Context context;
    private final int resource;

    public List<ApiCommentOut> getData() {
        return data;
    }
    private final List<ApiCommentOut> data;

    public CommentsAdapter(Context context, int resource, List<ApiCommentOut> data) {
        this.context = context;
        this.resource = resource;
        this.data = data;
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_comments_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentsAdapter.ViewHolder holder, int position) {
        holder.text.setText(data.get(position).getText());
        holder.date.setText(DateFormatter.formatDate(
                DateFormatter.timestampToDate(
                        data.get(position).getDate())));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public int getItemPosition(ApiCommentOut comment) {
        return data.indexOf(comment);
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.text)
        TextView text;

        @BindView(R.id.date)
        TextView date;

        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(context)
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ApiCommentOut apiComment = data.get(getAdapterPosition());
                                            ((DetailsActivity)context).deleteComment(apiComment);
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
    }
}
