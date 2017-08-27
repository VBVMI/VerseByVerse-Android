package org.versebyverseministry.vbvmi.fragments.answers;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.versebyverseministry.vbvmi.model.Article;
import org.versebyverseministry.vbvmi.model.Article_Table;

import java.util.List;

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
        List<Article> articles = SQLite.select().from(Article.class).orderBy(Article_Table.postedDate, false).queryList();

        adapter = new ArticlesRecyclerAdapter(articles);

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
        if (adapter != null) {
            adapter.setArticles(articles);
            if (adapter.getItemCount() > 0) {
                showList();
            } else {
                showLoading();
            }
        }
    }
}
