package org.versebyverseministry.vbvmi.fragments.studies.study;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.runtime.OnTableChangedListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.api.DatabaseManager;
import org.versebyverseministry.vbvmi.application.MainActivity;
import org.versebyverseministry.vbvmi.fragments.shared.AbstractFragment;
import org.versebyverseministry.vbvmi.model.Lesson;
import org.versebyverseministry.vbvmi.model.Lesson_Table;
import org.versebyverseministry.vbvmi.model.Study;
import org.versebyverseministry.vbvmi.model.Study_Table;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudyFragment extends AbstractFragment {
    private static final String ARG_STUDY_ID = "ARG_STUDY_ID";

    private Study study;

    private List<Lesson> lessons;

    private FlowContentObserver observer;

    private FlowContentObserver.OnModelStateChangedListener modelStateChangedListener;

    private OnTableChangedListener tableChangedListener;

    public StudyFragment() {
        // Required empty public constructor
    }

    private SectionsPagerAdapter mSectionsPagerAdapter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.container)
    ViewPager mViewPager;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

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
        unbinder = ButterKnife.bind(this, v);

        final Handler mainHandler = new Handler(getContext().getMainLooper());

        tableChangedListener = new OnTableChangedListener() {
            @Override
            public void onTableChanged(@Nullable Class<?> tableChanged, @NonNull BaseModel.Action action) {
                Log.d("LESSON", "table changed " + action.name() + " on Table " + tableChanged);
                if(tableChanged.toString().contains("Lesson")) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            configureView();
                        }
                    });
                }
            }
        };

        observer = DatabaseManager.observer;
        observer.setNotifyAllUris(false);
        observer.addOnTableChangedListener(tableChangedListener);

        configureView();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), new SECTION[]{SECTION.LESSONS, SECTION.INFO});
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

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
        observer.removeTableChangedListener(tableChangedListener);

        super.onDestroy();
    }

    private void configureView() {
        String studyId = getArguments().getString(ARG_STUDY_ID);
        study = SQLite.select().from(Study.class).where(Study_Table.id.eq(studyId)).querySingle();
        lessons = SQLite.select().from(Lesson.class).where(Lesson_Table.studyId.eq(studyId)).queryList();

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

        public SectionsPagerAdapter(FragmentManager fm, SECTION[] sections) {
            super(fm);
            this.sections = sections;
        }

        @Override
        public Fragment getItem(int position) {
            SECTION section = sections[position];
            switch (section) {
                case LESSONS:
                    return LessonsFragment.newInstance(study.id);
                case COMPLETED:
                    return null;
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
                    return "Lessons";
                case COMPLETED:
                    return "Completed";
                case INFO:
                    return "Info";
                default:
                    return null;
            }
        }
    }

}