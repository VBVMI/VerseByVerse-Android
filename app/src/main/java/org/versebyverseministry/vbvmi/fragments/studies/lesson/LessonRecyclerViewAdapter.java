package org.versebyverseministry.vbvmi.fragments.studies.lesson;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.versebyverseministry.vbvmi.FileHelpers;
import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.fragments.studies.study.LessonsFragment.OnLessonFragmentInteractionListener;
import org.versebyverseministry.vbvmi.model.Lesson;

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

    public void setLessons(List<Lesson> newLessons) {
        mValues = newLessons;
        this.notifyDataSetChanged();
    }

    public LessonRecyclerViewAdapter(List<Lesson> items, OnLessonFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        this.context = context;
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

        if (audioFile.exists()) {
            holder.audioFileImage.setColorFilter(ContextCompat.getColor(context, R.color.tableCellText));
        } else {
            holder.audioFileImage.setColorFilter(ContextCompat.getColor(context, R.color.dimGrey));
        }

        holder.audioFileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        holder.moreButton.setOnClickListener(v -> {
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
        public ImageView audioFileImage;

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
