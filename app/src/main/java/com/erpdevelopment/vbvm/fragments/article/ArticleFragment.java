package com.erpdevelopment.vbvm.fragments.article;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;

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

import org.algi.sugarloader.SugarLoader;

import java.io.UnsupportedEncodingException;

/**
 * Created by thomascarey on 29/08/17.
 */

public class ArticleFragment extends AbstractFragment {

    private static final String TAG = "ArticleFragment";
    private static final String ARG_ARTICLE_ID = "ARG_ARTICLE_ID";

    private String articleId;

    private Article article;

    private SugarLoader<Article> mLoader = new SugarLoader<Article>(TAG)
            .background(() -> SQLite.select().from(Article.class).where(Article_Table.id.eq(articleId)).querySingle())
            .onSuccess(a -> {
                article = a;
                reloadContent();
            });

    private Toolbar toolbar;
    private WebView webView;

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
            mLoader.init(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("");

        webView = view.findViewById(R.id.webView);

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

    private void reloadContent() {
        if (toolbar == null || webView == null || article == null) {
            return;
        }

        toolbar.setTitle(StringHelpers.fromHtmlString(article.title));

        String html = getContext().getString(R.string.html_container);

        String htmlBody = getContext().getString(R.string.article_html);
        htmlBody = htmlBody.replace("{{image_source}}", article.authorThumbnailSource);
        htmlBody = htmlBody.replace("{{image_alt}}", article.authorThumbnailAltText);

        String articleString = article.body;
        try {
            articleString = articleString.replaceAll("%", "%25");
            String result = java.net.URLDecoder.decode(articleString, "UTF-8");
            htmlBody = htmlBody.replace("{{content}}", result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            htmlBody = htmlBody.replace("{{content}}", "<p>Error loading content. Please try again later</p>");
        }

        webView.loadData(html.replace("{{content}}", htmlBody), "text/html", "UTF-8");
    }
}
