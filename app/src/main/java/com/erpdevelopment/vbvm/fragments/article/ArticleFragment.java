package com.erpdevelopment.vbvm.fragments.article;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.StringHelpers;
import com.erpdevelopment.vbvm.application.MainActivity;
import com.erpdevelopment.vbvm.fragments.shared.AbstractFragment;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.model.Article_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomascarey on 29/08/17.
 */

public class ArticleFragment extends AbstractFragment {

    private static final String TAG = "ArticleFragment";
    private static final String ARG_ARTICLE_ID = "ARG_ARTICLE_ID";

    private String articleId;

    private Article article;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.webView)
    WebView webView;

    public ArticleFragment() {

    }

    public static ArticleFragment newInstance(String articleId) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ARTICLE_ID, articleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            articleId = getArguments().getString(ARG_ARTICLE_ID);
            article = SQLite.select().from(Article.class).where(Article_Table.id.eq(articleId)).querySingle();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        unbinder = ButterKnife.bind(this, view);

        toolbar.setTitle(StringHelpers.fromHtmlString(article.title));

        String htmlBody = container.getContext().getString(R.string.article_html);
        htmlBody = htmlBody.replace("{{image_source}}", article.authorThumbnailSource);
        htmlBody = htmlBody.replace("{{image_alt}}", article.authorThumbnailAltText);

        String articleString = article.body;
        try {
            articleString = articleString.replaceAll("%", "%25");
            String result = java.net.URLDecoder.decode(articleString, "UTF-8");
            Log.d("DECODE", "didSelectArticle: " + result);
            htmlBody = htmlBody.replace("{{content}}", result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            htmlBody = htmlBody.replace("{{content}}", "<p>Error loading content. Please try again later</p>");
        }

        webView.loadData(htmlBody, "text/html", "UTF-8");

        MainActivity.get(getContext()).setSupportActionBar(toolbar);
        MainActivity.get(getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.get(getContext()).getMultistack().onBackPressed();
            }
        });

        return view;
    }
}
