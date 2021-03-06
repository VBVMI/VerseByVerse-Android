package com.erpdevelopment.vbvm.fragments.topics.articles;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.StringHelpers;
import com.erpdevelopment.vbvm.fragments.topics.QueryTopic;
import com.erpdevelopment.vbvm.fragments.topics.QueryTopicRecyclerViewAdapter;
import com.erpdevelopment.vbvm.fragments.topics.TopicSelectionListener;
import com.erpdevelopment.vbvm.model.Article;

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
 * Created by thomascarey on 27/08/17
 */

public class ArticlesRecyclerAdapter extends RecyclerView.Adapter<ArticlesRecyclerAdapter.ViewHolder> {


    private List<Article> articles;
    private Map<String, List<QueryTopic>> topicMap;
    private ArticleSelectionListener articleSelectionListener;
    private TopicSelectionListener topicSelectionListener;

    void setArticles(List<Article> articles, Map<String, List<QueryTopic>> topicMap) {
        this.topicMap = topicMap;
        this.articles = articles;
        this.notifyDataSetChanged();
    }

    ArticlesRecyclerAdapter(ArticleSelectionListener articleSelectionListener, TopicSelectionListener topicSelectionListener) {
        this.articles = new ArrayList<>();
        this.articleSelectionListener = articleSelectionListener;
        this.topicSelectionListener = topicSelectionListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view, topicSelectionListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Article article = articles.get(position);

        holder.titleView.setText(StringHelpers.fromHtmlString(article.title));
        Date date = new Date(TimeUnit.MILLISECONDS.convert(article.postedDate, TimeUnit.SECONDS));
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        holder.dateView.setText(dateFormat.format(date));
        holder.authorView.setText(StringHelpers.fromHtmlString(article.authorName));

        List<QueryTopic> topics = topicMap.get(article.id);

        if (topics != null && !topics.isEmpty()) {
            holder.adapter.setTopics(topics);
            holder.tagRecyclerView.setVisibility(View.VISIBLE);
        } else {
            holder.tagRecyclerView.setVisibility(View.GONE);
        }

        if (articleSelectionListener != null) {
            holder.itemView.setOnClickListener(v -> {
                articleSelectionListener.didSelectArticle(article);
            });
        }

        if (article.summary != null && article.summary.length() > 0) {
            holder.summaryTextView.setVisibility(View.VISIBLE);
            SpannableString string = new SpannableString(Html.fromHtml(article.summary));
            holder.summaryTextView.setText(string.toString().trim());
        } else {
            holder.summaryTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
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

        @BindView(R.id.summaryTextView)
        TextView summaryTextView;

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
