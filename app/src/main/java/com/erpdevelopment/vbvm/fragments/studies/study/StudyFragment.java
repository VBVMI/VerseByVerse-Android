package com.erpdevelopment.vbvm.fragments.studies.study;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erpdevelopment.vbvm.application.MainActivity;
import com.erpdevelopment.vbvm.fragments.shared.AbstractFragment;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import com.erpdevelopment.vbvm.R;

import org.versebyverseministry.models.Lesson;
import org.versebyverseministry.models.Lesson_Table;
import org.versebyverseministry.models.Study;
import org.versebyverseministry.models.Study_Table;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudyFragment extends AbstractFragment {
    private static final String ARG_STUDY_ID = "ARG_STUDY_ID";

    private Study study;

//    private FlowContentObserver observer;

    private FlowContentObserver.OnModelStateChangedListener modelStateChangedListener;

    private BroadcastReceiver lessonsChangedReceiver;

    public StudyFragment() {
        // Required empty public constructor
    }

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Toolbar toolbar;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    public boolean shouldBitmapUI() {
        return true;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param studyId The id of the Study.
     * @return A new instance of fragment StudyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudyFragment newInstance(String studyId) {
        StudyFragment fragment = new StudyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STUDY_ID, studyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_study, container, false);
        toolbar = v.findViewById(R.id.toolbar);
        mViewPager = v.findViewById(R.id.container);
        tabLayout = v.findViewById(R.id.tabs);

        final Handler mainHandler = new Handler(getContext().getMainLooper());

        lessonsChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        configureView();
                        mSectionsPagerAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(lessonsChangedReceiver, new IntentFilter(Lesson.updated()));

//        observer = DatabaseManager.observer;
//        observer.setNotifyAllUris(false);
//        observer.addOnTableChangedListener(tableChangedListener);

        configureView();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), new SECTION[]{SECTION.INFO, SECTION.LESSONS, SECTION.COMPLETED}, study);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(1);

        MainActivity.get(getContext()).setSupportActionBar(toolbar);
        MainActivity.get(getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.get(getContext()).getMultistack().onBackPressed();
            }
        });



        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("LESSON", "onPause: Lessons");
    }

    @Override
    public void onDestroy() {
        Log.d("LESSON", "onDestroy: Lessons");
        //observer.removeTableChangedListener(tableChangedListener);
        if (lessonsChangedReceiver != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(lessonsChangedReceiver);
            lessonsChangedReceiver = null;
        }
        super.onDestroy();
    }

    private void configureView() {
        String studyId = getArguments().getString(ARG_STUDY_ID);
        study = SQLite.select().from(Study.class).where(Study_Table.id.eq(studyId)).querySingle();

        if (toolbar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                toolbar.setTitle(Html.fromHtml(study.title, Html.FROM_HTML_MODE_LEGACY));
            } else {
                toolbar.setTitle(Html.fromHtml(study.title).toString());
            }
        }
    }

    public enum SECTION {
        LESSONS,
        COMPLETED,
        INFO
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SECTION[] sections;
        private Study study;

        public SectionsPagerAdapter(FragmentManager fm, SECTION[] sections, Study study) {
            super(fm);
            this.sections = sections;
            this.study = study;
        }

        @Override
        public Fragment getItem(int position) {
            SECTION section = sections[position];
            switch (section) {
                case LESSONS:
                    return LessonsFragment.newInstance(study.id, false);
                case COMPLETED:
                    return LessonsFragment.newInstance(study.id, true);
                case INFO:
                    return StudyInfoFragment.newInstance(study.id);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return sections.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            SECTION section = sections[position];
            switch (section) {
                case LESSONS:
                    return "Lessons (" + SQLite.selectCountOf().from(Lesson.class).where(Lesson_Table.studyId.eq(study.id)).and(Lesson_Table.complete.eq(false)).count() + ")";
                case COMPLETED:
                    return "Completed (" + SQLite.selectCountOf().from(Lesson.class).where(Lesson_Table.studyId.eq(study.id)).and(Lesson_Table.complete.eq(true)).count() + ")";
                case INFO:
                    return "Info";
                default:
                    return null;
            }
        }
    }

}
