package com.erpdevelopment.vbvm.fragments.media.videos;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.StringHelpers;
import com.erpdevelopment.vbvm.model.Channel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

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

        @BindView(R.id.background)
        View background;

        @BindView(R.id.title_view)
        TextView titleView;

        @BindView(R.id.count_text_view)
        TextView countTextView;

        @BindView(R.id.date_view)
        TextView dateView;

        public VideoSeriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
