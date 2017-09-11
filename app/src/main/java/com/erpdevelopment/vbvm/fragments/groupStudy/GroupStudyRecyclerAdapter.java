package com.erpdevelopment.vbvm.fragments.groupStudy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.StringHelpers;
import com.erpdevelopment.vbvm.model.GroupStudy;
import com.erpdevelopment.vbvm.model.Video;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomascarey on 11/09/17.
 */

public class GroupStudyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADER = 0;
    private static final int VIDEO = 1;

    private GroupStudy groupStudy;

    private VideoSelectionListener videoSelectionListener;
    private Context context;

    public GroupStudyRecyclerAdapter(VideoSelectionListener videoSelectionListener, Context context) {
        this.videoSelectionListener = videoSelectionListener;
        this.context = context;
    }

    public void setGroupStudy(GroupStudy groupStudy) {
        this.groupStudy = groupStudy;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_study_header, parent, false);
                return new HeaderViewHolder(view);
            }
            case VIDEO: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
                return new VideoViewHolder(view);
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder) {
            Glide.with(context).load(groupStudy.coverImage).into(((HeaderViewHolder) holder).imageView);
            ((HeaderViewHolder) holder).titleView.setText(StringHelpers.fromHtmlString(groupStudy.title));
            ((HeaderViewHolder) holder).downloadButton.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(groupStudy.pdf));
                context.startActivity(browserIntent);
            });
        } else {
            Video video = groupStudy.videos.get(position - 1);
            VideoViewHolder vh = (VideoViewHolder)holder;

            vh.titleView.setText(StringHelpers.fromHtmlString(video.title));
            Glide.with(context).load(video.thumbnailSource).into(vh.imageView);

            vh.itemView.setOnClickListener(v -> {
                if (videoSelectionListener != null) {
                    videoSelectionListener.videoTapped(video);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (groupStudy != null) {
            return 1 + groupStudy.videos.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return HEADER;
        } else {
            return VIDEO;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_view)
        ImageView imageView;

        @BindView(R.id.title_view)
        TextView titleView;

        @BindView(R.id.download_button)
        Button downloadButton;

        HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
