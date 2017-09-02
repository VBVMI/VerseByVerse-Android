package com.erpdevelopment.vbvm.fragments.topics.articles;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erpdevelopment.vbvm.application.MainActivity;
import com.erpdevelopment.vbvm.fragments.article.ArticleKey;
import com.erpdevelopment.vbvm.fragments.topics.AbstractListFragment;
import com.erpdevelopment.vbvm.fragments.topics.QueryTopic;
import com.erpdevelopment.vbvm.model.Article_Topic;
import com.erpdevelopment.vbvm.model.Topic_Table;
import com.erpdevelopment.vbvm.util.ServiceLocator;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.model.Article_Table;
import com.erpdevelopment.vbvm.model.Article_Topic_Table;
import com.erpdevelopment.vbvm.model.Topic;
import com.zhuinden.simplestack.BackstackDelegate;

import org.algi.sugarloader.SugarLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thomascarey on 27/08/17.
 */

public class ArticlesListFragment extends AbstractListFragment implements ArticleSelectionListener {

    ArticlesRecyclerAdapter adapter;

    private SugarLoader<ArticlesContainer> mLoader;

    public ArticlesListFragment() {

    }

    public static ArticlesListFragment newInstance() {
        ArticlesListFragment fragment = new ArticlesListFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoader = new SugarLoader<ArticlesContainer>("ArticlesListFragment")
                .background(() -> {
                    List<Article> articles = SQLite.select().from(Article.class).orderBy(Article_Table.postedDate, false).queryList();

                    FlowCursor cursor = SQLite.select().from(Article_Topic.class).as("A")
                            .join(Topic.class, Join.JoinType.INNER).as("T")
                            .on(Article_Topic_Table.topic_id
                                    .withTable(NameAlias.builder("A").build())
                                    .eq(Topic_Table.id.withTable(NameAlias.builder("T").build()))).orderBy(Article_Topic_Table.article_id, true).query();


                    Map<String, List<QueryTopic>> mappedTopics = new HashMap<>();
                    while (cursor.moveToNext()) {
                        String articleId = cursor.getString(1);
                        String topic = cursor.getString(4);
                        String topicId = cursor.getString(2);
                        QueryTopic qt = new QueryTopic(topic, topicId);
                        List<QueryTopic> topics = mappedTopics.get(articleId);
                        if (topics != null) {
                            topics.add(qt);
                        } else {
                            ArrayList<QueryTopic> list = new ArrayList<QueryTopic>();
                            list.add(qt);
                            mappedTopics.put(articleId, list);
                        }
                    }

                    return new ArticlesContainer(articles, mappedTopics);
                }).onSuccess(articlesContainer -> {
                    setArticles(articlesContainer.articles, articlesContainer.mappedTopics);
                });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoader.init(this);
    }

    @Override
    protected String tableName() {
        return "Article";
    }

    @Override
    protected void configureRecyclerView(RecyclerView recyclerView) {
        adapter = new ArticlesRecyclerAdapter(this);
        reloadData();
        if (adapter.getItemCount() > 0) {
            showList();
        } else {
            showLoading();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private class ArticlesContainer {
        public List<Article> articles;
        public Map<String, List<QueryTopic>> mappedTopics;

        public ArticlesContainer(List<Article> articles, Map<String, List<QueryTopic>> mappedTopics) {
            this.articles = articles;
            this.mappedTopics = mappedTopics;
        }
    }

    @Override
    protected void reloadData() {
        mLoader.restart(this);
    }

    private void setArticles(List<Article> articles, Map<String, List<QueryTopic>> mappedTopics) {
        if (adapter != null) {
            adapter.setArticles(articles, mappedTopics);
            if (adapter.getItemCount() > 0) {
                showList();
            } else {
                showLoading();
            }
        }
    }

    @Override
    public void didSelectArticle(Article article) {
        ((BackstackDelegate) ServiceLocator.getService(getContext(), MainActivity.StackType.TOPICS.name())).getBackstack().goTo(ArticleKey.create(article.id));
    }


}
