package com.erpdevelopment.vbvm.fragments.topics;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erpdevelopment.vbvm.R;

import java.util.List;

/**
 * Created by thomascarey on 28/08/17.
 */

public class QueryTopicRecyclerViewAdapter extends RecyclerView.Adapter<QueryTopicRecyclerViewAdapter.QueryTopicViewHolder> {

    private List<QueryTopic> topics;

    private TopicSelectionListener topicSelectionListener;

    public QueryTopicRecyclerViewAdapter(TopicSelectionListener topicSelectionListener) {
        this.topicSelectionListener = topicSelectionListener;
    }

    public void setTopics(List<QueryTopic> topics) {
        this.topics = topics;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QueryTopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        return new QueryTopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QueryTopicViewHolder holder, int position) {
        if (topics == null) {
            return;
        }
        QueryTopic topic = topics.get(position);

        holder.tagTextView.setText(topic.topic);

        holder.tagTextView.setOnClickListener(v -> {
            if (topicSelectionListener != null) {
                topicSelectionListener.didSelectTopic(topic.topicId);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (topics != null) {
            return topics.size();
        }
        return 0;
    }

    class QueryTopicViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView tagTextView;

        QueryTopicViewHolder(View itemView) {
            super(itemView);
            tagTextView = itemView.findViewById(R.id.tag_text);
        }
    }
}
