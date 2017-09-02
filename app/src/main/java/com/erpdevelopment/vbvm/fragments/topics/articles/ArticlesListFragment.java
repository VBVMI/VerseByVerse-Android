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
import com.erpdevelopment.vbvm.fragments.topics.TopicSelectionListener;
import com.erpdevelopment.vbvm.fragments.topics.TopicsKey;
import com.erpdevelopment.vbvm.model.Article_Topic;
import com.erpdevelopment.vbvm.model.Topic_Table;
import com.erpdevelopment.vbvm.util.ServiceLocator;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
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

public class ArticlesListFragment extends AbstractListFragment implements ArticleSelectionListener, TopicSelectionListener {

    private static final String ARG_TOPIC = "ARG_TOPIC";

    private String topicId;
    private String searchText;

    ArticlesRecyclerAdapter adapter;

    private SugarLoader<ArticlesContainer> mLoader = new SugarLoader<ArticlesContainer>("ArticlesListFragment")
            .background(() -> {
                List<Article> articles;

                if (topicId == null) {
                    if (searchText != null) {
                        articles = SQLite.select().from(Article.class).where(Article_Table.title.like("%" + searchText + "%")).or(Article_Table.body.like("%" + searchText + "%")).orderBy(Article_Table.postedDate, false).queryList();
                    } else {
                        articles = SQLite.select().from(Article.class).orderBy(Article_Table.postedDate, false).queryList();
                    }

                } else {

                    Where<Article> query = SQLite.select().from(Article.class).as("A")
                            .join(Article_Topic.class, Join.JoinType.INNER).as("T")
                            .on(Article_Table.id.withTable(NameAlias.builder("A").build())
                                    .eq(Article_Topic_Table.article_id.withTable(NameAlias.builder("T").build())))
                            .where(Article_Topic_Table.topic_id.withTable(NameAlias.builder("T").build())
                                    .eq(topicId));
                    if (searchText != null) {
                        OperatorGroup searchGroup = OperatorGroup.clause().and(Article_Table.title.like("%" + searchText + "%")).or(Article_Table.body.like("%" + searchText + "%"));
                        query = query.and(searchGroup);
                    }

                    articles = query.orderBy(Article_Table.postedDate, false).queryList();
                }


                FlowCursor cursor = SQLite.select().from(Article_Topic.class).as("A")
                        .join(Topic.class, Join.JoinType.INNER).as("T")
                        .on(Article_Topic_Table.topic_id
                                .withTable(NameAlias.builder("A").build())
                                .eq(Topic_Table.id.withTable(NameAlias.builder("T").build()))).orderBy(Article_Topic_Table.article_id, true).query();


                Map<String, List<QueryTopic>> mappedTopics = new HashMap<>();
                if (cursor != null) {
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
                }

                return new ArticlesContainer(articles, mappedTopics);
            }).onSuccess(articlesContainer -> {
                setArticles(articlesContainer.articles, articlesContainer.mappedTopics);
            });

    public ArticlesListFragment() {

    }

    @Override
    public void setSearchText(String searchText) {
        this.searchText = searchText;
        mLoader.restart(this);
    }

    public static ArticlesListFragment newInstance(String topicId) {
        ArticlesListFragment fragment = new ArticlesListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOPIC, topicId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topicId = getArguments().getString(ARG_TOPIC);
        }
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
        adapter = new ArticlesRecyclerAdapter(this, this);
        if (adapter.getItemCount() > 0) {
            showList();
        } else {
            showLoading();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void didSelectTopic(String topicId) {
        ((BackstackDelegate) ServiceLocator.getService(getContext(), MainActivity.StackType.TOPICS.name())).getBackstack().goTo(TopicsKey.createWithTopic(topicId));
    }

    private class ArticlesContainer {
        public List<Article> articles;
        Map<String, List<QueryTopic>> mappedTopics;

        ArticlesContainer(List<Article> articles, Map<String, List<QueryTopic>> mappedTopics) {
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
                showEmpty();
            }
        }
    }

    @Override
    public void didSelectArticle(Article article) {
        ((BackstackDelegate) ServiceLocator.getService(getContext(), MainActivity.StackType.TOPICS.name())).getBackstack().goTo(ArticleKey.create(article.id));
    }


}
