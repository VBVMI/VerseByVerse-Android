package com.erpdevelopment.vbvm.fragments.studies.lesson;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.erpdevelopment.vbvm.FileHelpers;
import com.erpdevelopment.vbvm.FontManager;
import com.erpdevelopment.vbvm.LessonResourceManager;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.fragments.studies.study.LessonsFragment.OnLessonFragmentInteractionListener;
import com.erpdevelopment.vbvm.model.Lesson;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Lesson} and makes a call to the
 * specified {@link OnLessonFragmentInteractionListener}.
 */
public class LessonRecyclerViewAdapter extends RecyclerView.Adapter<LessonRecyclerViewAdapter.ViewHolder> {

    private List<Lesson> mValues;
    private final OnLessonFragmentInteractionListener mListener;

    private final Context context;
    private Typeface iconFont;

    public void setLessons(List<Lesson> newLessons) {
        mValues = newLessons;
        this.notifyDataSetChanged();
    }

    public LessonRecyclerViewAdapter(List<Lesson> items, OnLessonFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        this.context = context;
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lesson, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        Lesson lesson = mValues.get(position);

        holder.mIdView.setText(lesson.lessonNumber);
        holder.mContentView.setText(lesson.description);

        File audioFile = FileHelpers.getAudioFileForLesson(context, lesson);

        holder.timeTextView.setText(lesson.audioLength);

        holder.audioFileImage.setTypeface(iconFont);

        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.3f);

        fadeOut.setDuration(500);
        fadeOut.setRepeatMode(Animation.REVERSE);
        fadeOut.setRepeatCount(Animation.INFINITE);
        holder.audioFileImage.clearAnimation();
        if (LessonResourceManager.getInstance().isDownloadingResource(lesson.id, FileHelpers.FILE_AUDIO)){
            holder.audioFileImage.setText(R.string.fa_download);
            holder.audioFileImage.setTextColor(ContextCompat.getColor(context, R.color.tableCellText));
            holder.audioFileImage.startAnimation(fadeOut);
            holder.audioFileImage.setOnClickListener(null);
        } else {
            holder.audioFileImage.setText(R.string.fa_play);
            LessonRecyclerViewAdapter adapter = this;
            holder.audioFileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListFragmentInteraction(holder.mItem);
                        adapter.notifyItemChanged(position);
                    }
                }
            });
        }

        if (audioFile.exists()) {
            holder.audioFileImage.setTextColor(ContextCompat.getColor(context, R.color.tableCellText));

        } else {
            holder.audioFileImage.setTextColor(ContextCompat.getColor(context, R.color.dimGrey));
        }

        holder.moreButton.setOnClickListener(v -> {
            showMore(lesson);
        });

        holder.mView.setOnClickListener(v ->{
            showMore(lesson);
        });
    }

    private void showMore(Lesson lesson) {
        if (null != mListener) {
            mListener.onMoreButton(lesson);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @BindView(R.id.id)
        public TextView mIdView;

        @BindView(R.id.content)
        public TextView mContentView;

        @BindView(R.id.audioFileImage)
        public TextView audioFileImage;

        @BindView(R.id.moreButton)
        ImageButton moreButton;

        @BindView(R.id.timeTextView)
        TextView timeTextView;

        public Lesson mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
