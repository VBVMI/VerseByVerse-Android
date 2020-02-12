package com.erpdevelopment.vbvm.fragments.answer;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.StringHelpers;
import com.erpdevelopment.vbvm.application.MainActivity;
import com.erpdevelopment.vbvm.fragments.shared.AbstractFragment;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.model.Answer_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.algi.sugarloader.SugarLoader;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomascarey on 2/09/17.
 */

public class AnswerFragment extends AbstractFragment {

    private static final String TAG = "AnswerFragment";
    private static final String ARG_ANSWER_ID = "ARG_ANSWER_ID";

    private String answerId;
    private Answer answer;

    private SugarLoader<Answer> mLoader = new SugarLoader<Answer>(TAG)
            .background(() -> SQLite.select().from(Answer.class).where(Answer_Table.id.eq(answerId)).querySingle()).onSuccess(a -> {
                answer = a;
                reloadContent();
            });

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.webView)
    WebView webView;

    public AnswerFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article, container, false);
        unbinder = ButterKnife.bind(this, view);

        toolbar.setTitle("");

        reloadContent();

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

    public static AnswerFragment newInstance(String answerId) {
        AnswerFragment fragment = new AnswerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ANSWER_ID, answerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            answerId = getArguments().getString(ARG_ANSWER_ID);
            mLoader.init(this);
        }
    }

    private void reloadContent() {
        if (toolbar == null || webView == null || answer == null) {
            return;
        }

        toolbar.setTitle(StringHelpers.fromHtmlString(answer.title));

        String html = getContext().getString(R.string.html_container);

        String htmlBody = getContext().getString(R.string.answer_html);

        String answerString = answer.body;
        try {
            answerString = answerString.replaceAll("%", "%25");
            String result = java.net.URLDecoder.decode(answerString, "UTF-8");
            htmlBody = htmlBody.replace("{{content}}", result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            htmlBody = htmlBody.replace("{{content}}", "<p>Error loading content. Please try again later</p>");
        }

        webView.loadData(html.replace("{{content}}", htmlBody), "text/html", "UTF-8");
    }
}
