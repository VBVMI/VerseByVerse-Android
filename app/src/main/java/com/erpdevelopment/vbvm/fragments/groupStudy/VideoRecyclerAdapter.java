package com.erpdevelopment.vbvm.fragments.groupStudy;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.StringHelpers;

import org.versebyverseministry.models.Channel;
import org.versebyverseministry.models.Video;

/**
 * Created by thomascarey on 23/09/17.
 */

public class VideoRecyclerAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    private Channel channel;
    private Context context;
    private VideoSelectionListener videoSelectionListener;

    VideoRecyclerAdapter(VideoSelectionListener videoSelectionListener, Context context) {
        this.videoSelectionListener = videoSelectionListener;
        this.context = context;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
        notifyDataSetChanged();
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        Video video = channel.videos.get(position);
        VideoViewHolder vh = holder;

        vh.titleView.setText(StringHelpers.fromHtmlString(video.title));
        Glide.with(context).load(video.thumbnailSource).into(vh.imageView);

        vh.itemView.setOnClickListener(v -> {
            if (videoSelectionListener != null) {
                videoSelectionListener.videoTapped(video);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (channel != null) {
            return channel.videos.size();
        }
        return 0;
    }
}
