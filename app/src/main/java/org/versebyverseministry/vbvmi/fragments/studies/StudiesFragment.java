package org.versebyverseministry.vbvmi.fragments.studies;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.runtime.OnTableChangedListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.zhuinden.simplestack.BackstackDelegate;

import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.api.APIManager;
import org.versebyverseministry.vbvmi.api.DatabaseManager;
import org.versebyverseministry.vbvmi.application.MainActivity;
import org.versebyverseministry.vbvmi.fragments.shared.AbstractFragment;
import org.versebyverseministry.vbvmi.fragments.studies.study.StudyKey;
import org.versebyverseministry.vbvmi.model.Category;
import org.versebyverseministry.vbvmi.model.Category$$Parcelable;
import org.versebyverseministry.vbvmi.model.Category_Table;
import org.versebyverseministry.vbvmi.model.Lesson;
import org.versebyverseministry.vbvmi.model.Study;
import org.versebyverseministry.vbvmi.model.Study_Table;
import org.versebyverseministry.vbvmi.util.ServiceLocator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link StudiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudiesFragment extends AbstractFragment {


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @BindView(R.id.studiesContainer)
    ViewPager mViewPager;

    @BindView(R.id.studiesToolar)
    Toolbar toolbar;

    @BindView(R.id.studiesTabs)
    TabLayout tabLayout;

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
        mSectionsPagerAdapter = new SectionsPagerAdapter(this.getChildFragmentManager(), categories);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_studies_view, container, false);
        unbinder = ButterKnife.bind(this, view);

        toolbar.setTitle("Studies");

        configureCategoryPager();

        tabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }



    @Override
    public void onDetach() {
        super.onDetach();
        mSectionsPagerAdapter = null;
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_CATEGORY = "ARG_CATEGORY";
        private Category category;

        private RecyclerView view;

        OnTableChangedListener tableChangedListener;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(Category category) {
            PlaceholderFragment fragment = new PlaceholderFragment();
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

            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            int numberOfColumns = (int)(dpWidth / 120);

            if (rootView instanceof RecyclerView) {
                Context context = rootView.getContext();
                RecyclerView recyclerView = (RecyclerView) rootView;
                this.view = recyclerView;
                recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));
                recyclerView.setAdapter(new MyStudyRecyclerViewAdapter(studies, numberOfColumns, new MyStudyRecyclerViewAdapter.OnStudyInteractionListener() {
                    @Override
                    public void studyClicked(Study study) {
                        APIManager.getInstance().downloadLessons(study.id);
                        ((BackstackDelegate)ServiceLocator.getService(getContext(), MainActivity.StackType.STUDIES.name())).getBackstack().goTo(StudyKey.create(study.id));
                    }
                }));
            }

            final Handler mainHandler = new Handler(getContext().getMainLooper());

            tableChangedListener = new OnTableChangedListener() {
                @Override
                public void onTableChanged(@Nullable Class<?> tableChanged, @NonNull BaseModel.Action action) {
                    if(tableChanged.toString().contains("Study")) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                reloadData();
                            }
                        });
                    }
                }
            };

            DatabaseManager.observer.addOnTableChangedListener(tableChangedListener);
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
            DatabaseManager.observer.removeTableChangedListener(tableChangedListener);
            Log.d("STUDIESFRAGMENT", "removed table change listener");
            tableChangedListener = null;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private List<Category> categories;

        public SectionsPagerAdapter(FragmentManager fm, List<Category> categories) {
            super(fm);
            this.categories = categories;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(categories.get(position));
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
