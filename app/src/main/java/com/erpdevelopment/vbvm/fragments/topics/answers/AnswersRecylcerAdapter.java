package com.erpdevelopment.vbvm.fragments.topics.answers;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.StringHelpers;
import com.erpdevelopment.vbvm.fragments.topics.QueryTopic;
import com.erpdevelopment.vbvm.fragments.topics.QueryTopicRecyclerViewAdapter;
import com.erpdevelopment.vbvm.fragments.topics.TopicSelectionListener;
import org.versebyverseministry.models.Answer;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    void setAnswers(List<Answer> answers, Map<String, List<QueryTopic>> topicMap) {
        this.topicMap = topicMap;
        this.answers = answers;
        this.notifyDataSetChanged();
    }

    @NonNull
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

        TextView titleView;
        TextView authorView;
        TextView dateView;
        RecyclerView tagRecyclerView;

        QueryTopicRecyclerViewAdapter adapter;

        public ViewHolder(View itemView, TopicSelectionListener topicSelectionListener) {
            super(itemView);
            titleView = itemView.findViewById(R.id.title_view);
            authorView = itemView.findViewById(R.id.author_view);
            dateView = itemView.findViewById(R.id.date_view);
            tagRecyclerView = itemView.findViewById(R.id.tag_recycler_view);

            adapter = new QueryTopicRecyclerViewAdapter(topicSelectionListener);
            tagRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            tagRecyclerView.setAdapter(adapter);
        }
    }

}
