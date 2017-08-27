package com.erpdevelopment.vbvm.fragments.answers;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.erpdevelopment.vbvm.model.Article_Topic;
import com.erpdevelopment.vbvm.model.Topic_Table;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.model.Article_Table;
import com.erpdevelopment.vbvm.model.Article_Topic_Table;
import com.erpdevelopment.vbvm.model.Topic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thomascarey on 27/08/17.
 */

public class ArticlesListFragment extends AbstractListFragment {

    ArticlesRecyclerAdapter adapter;

    public ArticlesListFragment() {

    }

    public static ArticlesListFragment newInstance() {
        ArticlesListFragment fragment = new ArticlesListFragment();

        return fragment;
    }

    @Override
    String tableName() {
        return "Article";
    }

    @Override
    protected void configureRecyclerView(RecyclerView recyclerView) {
        adapter = new ArticlesRecyclerAdapter();
        reloadData();
        if (adapter.getItemCount() > 0) {
            showList();
        } else {
            showLoading();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    void reloadData() {
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

        if (adapter != null) {
            adapter.setArticles(articles, mappedTopics);
            if (adapter.getItemCount() > 0) {
                showList();
            } else {
                showLoading();
            }
        }
    }

    public class QueryTopic {
        String topic;
        String topicId;

        public QueryTopic(String topic, String topicId) {
            this.topic = topic;
            this.topicId = topicId;
        }
    }
}
