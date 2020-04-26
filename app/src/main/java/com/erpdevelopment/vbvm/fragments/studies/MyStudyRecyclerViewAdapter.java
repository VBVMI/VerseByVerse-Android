package com.erpdevelopment.vbvm.fragments.studies;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.views.SquareImageView;

import org.versebyverseministry.models.Study;

import java.util.List;

/**
 * Created by thomascarey on 23/07/17.
 */

public class MyStudyRecyclerViewAdapter extends RecyclerView.Adapter<MyStudyRecyclerViewAdapter.ViewHolder> {

    public interface OnStudyInteractionListener {
        public void studyClicked(Study study);
    }

    private List<Study> studies;

    private OnStudyInteractionListener listener;
    private int numberOfColumns;

    public void setStudies(List<Study> newStudies){
        studies = newStudies;
        this.notifyDataSetChanged();
    }

    MyStudyRecyclerViewAdapter(List<Study> studies, int numberOfColumns, OnStudyInteractionListener listener) {
        this.studies = studies;
        this.listener = listener;
        this.numberOfColumns = numberOfColumns;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bible_study, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Study study = studies.get(position);
        holder.study = study;
        holder.textView.setText(Html.fromHtml(holder.study.title));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        Context context = holder.mView.getContext();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

        int imageWidth = displayMetrics.widthPixels / numberOfColumns;
        String studyImageURL = holder.study.imageForWidth(imageWidth);
        if (studyImageURL != null) {
            Glide.with(context).load(studyImageURL).into(holder.imageView);
        } else {
            Glide.with(context).load(holder.study.thumbnailSource).into(holder.imageView);
        }

        holder.topImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.studyClicked(study);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return studies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;

        TextView textView;
        private SquareImageView imageView;
        View topImageView;

        private Study study;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.bible_study_image_view);
            topImageView = itemView.findViewById(R.id.topImageView);
        }
    }
}
