package com.erpdevelopment.vbvm.fragments.media.videos;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.StringHelpers;
import org.versebyverseministry.models.Channel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by thomascarey on 23/09/17.
 */

public class VideoSeriesRecylcerAdapter extends RecyclerView.Adapter<VideoSeriesRecylcerAdapter.VideoSeriesViewHolder> {

    private List<Channel> videoSeries;
    private VideoSeriesSelectionListener videoSeriesSelectionListener;

    VideoSeriesRecylcerAdapter(VideoSeriesSelectionListener selectionListener) {
        this.videoSeriesSelectionListener = selectionListener;
        this.videoSeries = new ArrayList<>();
    }

    void setVideoSeries(List<Channel> videoSeries) {
        this.videoSeries = videoSeries;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoSeriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_series, parent, false);
        return new VideoSeriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoSeriesViewHolder holder, int position) {
        Channel c = videoSeries.get(position);
        holder.titleView.setText(StringHelpers.fromHtmlString(c.title));

        holder.countTextView.setText("" + c.videoCount + " videos");

        Date date = new Date(TimeUnit.MILLISECONDS.convert(c.postedDate, TimeUnit.SECONDS));
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        holder.dateView.setText(dateFormat.format(date));

        holder.background.setOnClickListener(v -> {
            if (videoSeriesSelectionListener != null) {
                videoSeriesSelectionListener.didSelectVideoSeries(c);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoSeries.size();
    }

    class VideoSeriesViewHolder extends RecyclerView.ViewHolder {

        View background;
        TextView titleView;
        TextView countTextView;
        TextView dateView;

        VideoSeriesViewHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.background);
            titleView = itemView.findViewById(R.id.title_view);
            countTextView = itemView.findViewById(R.id.count_text_view);
            dateView = itemView.findViewById(R.id.date_view);
        }
    }
}
