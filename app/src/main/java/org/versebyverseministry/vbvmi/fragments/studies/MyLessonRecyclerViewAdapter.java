package org.versebyverseministry.vbvmi.fragments.studies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.fragments.studies.LessonFragment.OnListFragmentInteractionListener;
import org.versebyverseministry.vbvmi.model.Lesson;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Lesson} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyLessonRecyclerViewAdapter extends RecyclerView.Adapter<MyLessonRecyclerViewAdapter.ViewHolder> {

    private List<Lesson> mValues;
    private final OnListFragmentInteractionListener mListener;

    public void setLessons(List<Lesson> newLessons) {
        mValues = newLessons;
        this.notifyDataSetChanged();
    }

    public MyLessonRecyclerViewAdapter(List<Lesson> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_lesson, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).lessonNumber);
        holder.mContentView.setText(mValues.get(position).description);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public Lesson mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
