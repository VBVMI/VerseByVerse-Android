package com.erpdevelopment.vbvm.fragments.groupStudy;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.erpdevelopment.vbvm.R;

/**
 * Created by thomascarey on 11/09/17.
 */

class VideoViewHolder extends RecyclerView.ViewHolder {

    TextView titleView;
    ImageView imageView;
    private TextView countTextView;
    private TextView dateView;
    ProgressBar progressBar;

    VideoViewHolder(View itemView) {
        super(itemView);
        titleView = itemView.findViewById(R.id.title_view);
        imageView = itemView.findViewById(R.id.poster_image_view);
        countTextView = itemView.findViewById(R.id.count_text_view);
        dateView = itemView.findViewById(R.id.date_view);
        progressBar = itemView.findViewById(R.id.progressBar);
    }
}
