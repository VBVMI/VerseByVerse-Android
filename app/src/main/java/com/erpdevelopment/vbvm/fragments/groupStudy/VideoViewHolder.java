package com.erpdevelopment.vbvm.fragments.groupStudy;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.erpdevelopment.vbvm.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomascarey on 11/09/17.
 */

public class VideoViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.title_view)
    TextView titleView;

    @BindView(R.id.poster_image_view)
    ImageView imageView;

    @BindView(R.id.count_text_view)
    TextView countTextView;

    @BindView(R.id.date_view)
    TextView dateView;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public VideoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
