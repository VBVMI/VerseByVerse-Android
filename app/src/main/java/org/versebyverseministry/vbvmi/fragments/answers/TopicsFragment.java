package org.versebyverseministry.vbvmi.fragments.answers;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.api.APIManager;
import org.versebyverseministry.vbvmi.fragments.shared.AbstractFragment;
import org.versebyverseministry.vbvmi.model.Topic;
import org.versebyverseministry.vbvmi.views.LoadingView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopicsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopicsFragment extends AbstractFragment {
    private static final String TAG = "TopicsFragment";
    private static final String ARG_TOPIC_ID = "ARG_TOPIC_ID";

    private String topicId;

    @BindView(R.id.studiesContainer)
    ViewPager mViewPager;

    @BindView(R.id.studiesToolar)
    Toolbar toolbar;

    @BindView(R.id.studiesTabs)
    TabLayout tabLayout;


    private static Date lastRequestDate = null;

    public TopicsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AnswersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopicsFragment newInstance(String topicId) {
        TopicsFragment fragment = new TopicsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOPIC_ID, topicId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topicId = getArguments().getString(ARG_TOPIC_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_topics, container, false);
        unbinder = ButterKnife.bind(this, view);

        toolbar.setTitle(R.string.title_topics);

//        FlowCursor cursor1 = SQLite.select().from(Article_Topic.class).as("A")
//                .join(Topic.class, Join.JoinType.INNER).as("T")
//                .on(Article_Topic_Table.topic_id
//                        .withTable(NameAlias.builder("A").build())
//                        .eq(Topic_Table.id.withTable(NameAlias.builder("T").build()))).orderBy(Article_Topic_Table.article_id, true).query();
//        cursor1.moveToFirst();
//        cursor1.moveToNext();
//        cursor1.getCount();
//String str = "_id:" + cursor1.getInt(0) + " aid:" + cursor1.getInt(1) + " topic:" + cursor1.getString(4);

        if(lastRequestDate == null || TimeUnit.MILLISECONDS.toSeconds((new Date()).getTime() - lastRequestDate.getTime()) > 3600 ) {
            lastRequestDate = new Date();
            APIManager.getInstance().downloadArticles(success -> {
                Log.d(TAG, "Downloaded all them articles (" + success + ")");
            });
        }

        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(this.getChildFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    private enum Topics {
        ANSWERS,
        ARTICLES
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private List<Topics> topics = Arrays.asList(Topics.ARTICLES); //Topics.ANSWERS,

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Topics topic = topics.get(position);
            switch (topic) {
                case ANSWERS: return null;
                case ARTICLES: return ArticlesListFragment.newInstance();
                default: return null;
            }
        }

        @Override
        public int getCount() {
            return topics.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Topics topic = topics.get(position);
            switch (topic) {
                case ANSWERS: return "Answers";
                case ARTICLES: return "Articles";
                default: return "";
            }
        }
    }

}
