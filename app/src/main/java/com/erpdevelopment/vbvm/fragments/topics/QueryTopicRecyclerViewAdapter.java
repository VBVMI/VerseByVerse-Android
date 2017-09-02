package com.erpdevelopment.vbvm.fragments.topics;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erpdevelopment.vbvm.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomascarey on 28/08/17.
 */

public class QueryTopicRecyclerViewAdapter extends RecyclerView.Adapter<QueryTopicRecyclerViewAdapter.QueryTopicViewHolder> {

    private List<QueryTopic> topics;

    public void setTopics(List<QueryTopic> topics) {
        this.topics = topics;
        notifyDataSetChanged();
    }

    @Override
    public QueryTopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        return new QueryTopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QueryTopicViewHolder holder, int position) {
        if (topics == null) {
            return;
        }
        QueryTopic topic = topics.get(position);

        holder.tagTextView.setText(topic.topic);
    }

    @Override
    public int getItemCount() {
        if (topics != null) {
            return topics.size();
        }
        return 0;
    }

    public class QueryTopicViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tag_text)
        AppCompatTextView tagTextView;

        public QueryTopicViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
