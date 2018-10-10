package com.erpdevelopment.vbvm.fragments.studies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.raizlabs.android.dbflow.runtime.OnTableChangedListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.zhuinden.simplestack.BackstackDelegate;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.api.APIManager;
import com.erpdevelopment.vbvm.api.DatabaseManager;
import com.erpdevelopment.vbvm.application.MainActivity;
import com.erpdevelopment.vbvm.fragments.shared.AbstractFragment;
import com.erpdevelopment.vbvm.fragments.studies.study.StudyKey;
import com.erpdevelopment.vbvm.model.Category;
import com.erpdevelopment.vbvm.model.Category$$Parcelable;
import com.erpdevelopment.vbvm.model.Category_Table;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.model.Study_Table;
import com.erpdevelopment.vbvm.util.ServiceLocator;
import com.erpdevelopment.vbvm.views.LoadingView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link StudiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudiesFragment extends AbstractFragment {

    private static final String TAG = "StudiesFragment";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private static boolean categoriesCompleted = false;
    private static boolean studiesCompleted = false;
    private static Date lastRequestDate = null;

    private static Runnable activeRunner = null;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @BindView(R.id.studiesContainer)
    ViewPager mViewPager;

    @BindView(R.id.studiesToolar)
    Toolbar toolbar;

    @BindView(R.id.studiesTabs)
    TabLayout tabLayout;

    @BindView(R.id.loading_view)
    LoadingView loadingView;

    public StudiesFragment() {
        // Required empty public constructor
    }

    @Override
    public boolean shouldBitmapUI() {
        return true;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StudiesView.
     */
    public static StudiesFragment newInstance() {
        StudiesFragment fragment = new StudiesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    private void configureCategoryPager() {
        List<Category> categories = SQLite.select().from(Category.class).orderBy(Category_Table.order, true).queryList();
        if (SQLite.selectCountOf().from(Study.class).count() > 0) {
            mSectionsPagerAdapter.setCategories(categories);
            mViewPager.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
            Log.d(TAG, "configureCategoryPager: " + this);
        }
    }

    private boolean hasContent() {
        if (SQLite.selectCountOf().from(Study.class).count() > 0 && SQLite.selectCountOf().from(Category.class).count() > 0)
            return true;
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_studies_root, container, false);
        unbinder = ButterKnife.bind(this, view);

        toolbar.setTitle(R.string.title_studies);

        mSectionsPagerAdapter = new SectionsPagerAdapter(this.getChildFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        configureCategoryPager();

        tabLayout.setupWithViewPager(mViewPager);

        if (!hasContent()) {
            loadingView.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.GONE);
        }

        if(lastRequestDate == null || TimeUnit.MILLISECONDS.toSeconds((new Date()).getTime() - lastRequestDate.getTime()) > 300 ) {
            categoriesCompleted = false;
            studiesCompleted = false;
            lastRequestDate = new Date();
            Log.d(TAG, "onCreateView: ");
            APIManager.getInstance().downloadCategories(success -> {
                // Categories downloaded... refresh that screen!
                if (success) {
                    //configureCategoryPager();
                    categoriesCompleted = true;
                    if (studiesCompleted && categoriesCompleted && StudiesFragment.activeRunner != null) {
                        StudiesFragment.activeRunner.run();
                    }
                }
            });

            APIManager.getInstance().downloadStudies(success -> {
                // Studies downloaded... refresh!
                if (success) {
                    studiesCompleted = true;
                    if (studiesCompleted && categoriesCompleted && StudiesFragment.activeRunner != null) {
                        StudiesFragment.activeRunner.run();
                    }
                }
            });
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.d(TAG, "onAttach: " + this);
        activeRunner = new Runnable() {
            @Override
            public void run() {
                if (!isDetached())
                    configureCategoryPager();
            }
        };
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mSectionsPagerAdapter = null;
        Log.d(TAG, "onDetach: " + this);
        activeRunner = null;
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class StudiesListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_CATEGORY = "ARG_CATEGORY";
        private Category category;

        private RecyclerView view;

        BroadcastReceiver studiesReceiver;

        public StudiesListFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static StudiesListFragment newInstance(Category category) {
            StudiesListFragment fragment = new StudiesListFragment();
            Bundle args = new Bundle();
            args.putParcelable(ARG_CATEGORY, new Category$$Parcelable(category));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_studies, container, false);

            category = ((Category$$Parcelable)getArguments().getParcelable(ARG_CATEGORY)).getParcel();

            final List<Study> studies = SQLite.select().from(Study.class).where(Study_Table.category.eq(category.id)).orderBy(Study_Table.bibleIndex, true).queryList();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

            Log.d("Placeholder", "onCreateView: found " + studies.size() + " studies");

            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

            float imageWidth = Math.max((dpWidth / 4) - 1, 120);

            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;

            float imageHeight = dpHeight / 3;
            imageWidth = Math.min(imageHeight, imageWidth);

            int numberOfColumns = (int)(dpWidth / imageWidth);

            // on a large device go with 4 columns?

            if (rootView instanceof RecyclerView) {
                Context context = rootView.getContext();
                RecyclerView recyclerView = (RecyclerView) rootView;
                this.view = recyclerView;
                recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));
                recyclerView.setAdapter(new MyStudyRecyclerViewAdapter(studies, numberOfColumns, new MyStudyRecyclerViewAdapter.OnStudyInteractionListener() {
                    @Override
                    public void studyClicked(Study study) {
                        APIManager.getInstance().downloadLessons(study.id, success -> {
                            Log.d(TAG, "Downloaded the lessons successfully " + success);
                        });
                        ((BackstackDelegate)ServiceLocator.getService(getContext(), MainActivity.StackType.STUDIES.name())).getBackstack().goTo(StudyKey.create(study.id));
                    }
                }));
            }

            final Handler mainHandler = new Handler(getContext().getMainLooper());

            studiesReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    mainHandler.post(() -> reloadData());
                }
            };

            LocalBroadcastManager.getInstance(getContext()).registerReceiver(studiesReceiver, new IntentFilter(Study.updated()));

            //DatabaseManager.observer.addOnTableChangedListener(tableChangedListener);
            return rootView;
        }

        public void reloadData() {
            MyStudyRecyclerViewAdapter adapter = (MyStudyRecyclerViewAdapter)view.getAdapter();
            final List<Study> studies = SQLite.select().from(Study.class).where(Study_Table.category.eq(category.id)).orderBy(Study_Table.bibleIndex, true).queryList();
            adapter.setStudies(studies);
        }

        @Override
        public void onDetach() {
            super.onDetach();
            //DatabaseManager.observer.removeTableChangedListener(tableChangedListener);
            Log.d("STUDIESFRAGMENT", "removed table change listener");
            if (studiesReceiver != null) {
                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(studiesReceiver);
                studiesReceiver = null;
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        private List<Category> categories = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setCategories(List<Category> categories) {
            if (categories == null) {
                categories = new ArrayList<>();
                notifyDataSetChanged();
                return;
            }
            this.categories = categories;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return StudiesListFragment.newInstance(categories.get(position));
        }

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Category category = categories.get(position);
            return category.name;
        }
    }

}
