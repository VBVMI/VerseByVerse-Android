package com.erpdevelopment.vbvm.fragments.topics.answers;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.StringHelpers;
import com.erpdevelopment.vbvm.fragments.topics.QueryTopic;
import com.erpdevelopment.vbvm.fragments.topics.QueryTopicRecyclerViewAdapter;
import com.erpdevelopment.vbvm.fragments.topics.TopicSelectionListener;
import com.erpdevelopment.vbvm.model.Answer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomascarey on 2/09/17
 */

public class AnswersRecylcerAdapter extends RecyclerView.Adapter<AnswersRecylcerAdapter.ViewHolder> {

    private List<Answer> answers;
    private Map<String, List<QueryTopic>> topicMap;
    private AnswerSelectionListener answerSelectionListener;
    private TopicSelectionListener topicSelectionListener;

    AnswersRecylcerAdapter(AnswerSelectionListener answerSelectionListener, TopicSelectionListener topicSelectionListener) {
        this.answerSelectionListener = answerSelectionListener;
        this.topicSelectionListener = topicSelectionListener;
        this.answers = new ArrayList<>();
    }

    public void setAnswers(List<Answer> answers, Map<String, List<QueryTopic>> topicMap) {
        this.topicMap = topicMap;
        this.answers = answers;
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer, parent, false);
        return new ViewHolder(view, topicSelectionListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Answer answer = answers.get(position);

        holder.titleView.setText(StringHelpers.fromHtmlString(answer.title));
        Date date = new Date(TimeUnit.MILLISECONDS.convert(answer.postedDate, TimeUnit.SECONDS));
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        holder.dateView.setText(dateFormat.format(date));
        holder.authorView.setText(StringHelpers.fromHtmlString(answer.authorName));

        List<QueryTopic> topics = topicMap.get(answer.id);

        if (topics != null && !topics.isEmpty()) {
            holder.adapter.setTopics(topics);
            holder.tagRecyclerView.setVisibility(View.VISIBLE);
        } else {
            holder.tagRecyclerView.setVisibility(View.GONE);
        }

        if (answerSelectionListener != null) {
            holder.itemView.setOnClickListener(v -> {
                answerSelectionListener.didSelectAnswer(answer);
            });
        }
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title_view)
        TextView titleView;

        @BindView(R.id.author_view)
        TextView authorView;

        @BindView(R.id.date_view)
        TextView dateView;

        @BindView(R.id.tag_recycler_view)
        RecyclerView tagRecyclerView;

        QueryTopicRecyclerViewAdapter adapter;

        public ViewHolder(View itemView, TopicSelectionListener topicSelectionListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            adapter = new QueryTopicRecyclerViewAdapter(topicSelectionListener);
            tagRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            tagRecyclerView.setAdapter(adapter);
        }
    }

}
