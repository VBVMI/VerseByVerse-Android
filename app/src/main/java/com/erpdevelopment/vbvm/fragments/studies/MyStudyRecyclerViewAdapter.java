package com.erpdevelopment.vbvm.fragments.studies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.views.SquareImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    public MyStudyRecyclerViewAdapter(List<Study> studies, int numberOfColumns, OnStudyInteractionListener listener) {
        this.studies = studies;
        this.listener = listener;
        this.numberOfColumns = numberOfColumns;
    }

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
        public View mView;

        @BindView(R.id.textView)
        public TextView textView;

        @BindView(R.id.bible_study_image_view)
        public SquareImageView imageView;

        @BindView(R.id.topImageView)
        public View topImageView;

        public Study study;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
