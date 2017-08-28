package com.erpdevelopment.vbvm.fragments.answers;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.StringHelpers;
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
 * Created by thomascarey on 27/08/17.
 */

public class ArticlesRecyclerAdapter extends RecyclerView.Adapter<ArticlesRecyclerAdapter.ViewHolder> {


    private List<Article> articles;
    private Map<String, List<ArticlesListFragment.QueryTopic>> topicMap;

    public void setArticles(List<Article> articles, Map<String, List<ArticlesListFragment.QueryTopic>> topicMap) {
        this.topicMap = topicMap;
        this.articles = articles;
        this.notifyDataSetChanged();
    }

    public ArticlesRecyclerAdapter() {
        this.articles = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Article article = articles.get(position);

        holder.titleView.setText(StringHelpers.fromHtmlString(article.title));
        Date date = new Date(TimeUnit.MILLISECONDS.convert(article.postedDate, TimeUnit.SECONDS));
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        holder.dateView.setText(dateFormat.format(date));
        holder.authorView.setText(StringHelpers.fromHtmlString(article.authorName));

        List<ArticlesListFragment.QueryTopic> topics = topicMap.get(article.id);

        if (topics != null && !topics.isEmpty()) {
            holder.adapter.setTopics(topics);
            holder.tagRecyclerView.setVisibility(View.VISIBLE);
        } else {
            holder.tagRecyclerView.setVisibility(View.GONE);
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

        QueryTopicRecyclerViewAdapter adapter = new QueryTopicRecyclerViewAdapter();

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            tagRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            tagRecyclerView.setAdapter(adapter);
        }
    }

}
