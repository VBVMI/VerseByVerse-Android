package org.versebyverseministry.vbvmi.fragments.studies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;

import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.api.APIManager;
import org.versebyverseministry.vbvmi.application.Key;
import org.versebyverseministry.vbvmi.application.MainActivity;
import org.versebyverseministry.vbvmi.fragments.shared.AbstractFragment;
import org.versebyverseministry.vbvmi.model.Category;
import org.versebyverseministry.vbvmi.model.Category$$Parcelable;
import org.versebyverseministry.vbvmi.model.Category_Table;
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

    private FlowContentObserver observer;

    private View cachedView;

    public StudiesFragment() {
        // Required empty public constructor
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

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.studiesToolar);
        toolbar.setTitle("Studies");
//        // Create the adapter that will return a fragment for each of the three
//        // primary sections of the activity.
//



//        final StudiesView me = this;
//        final Handler mainHandler = new Handler(getContext().getMainLooper());
//
//        observer.addModelChangeListener(new FlowContentObserver.OnModelStateChangedListener() {
//            @Override
//            public void onModelStateChanged(@Nullable Class<?> table, BaseModel.Action action, @NonNull SQLOperator[] primaryKeyValues) {
//                Log.d("VBVMI", "onModelStateChanged: ");
//
//                mainHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        me.configureCategoryPager();
//                    }
//                });
//
//            }
//        });


//
//        // Set up the ViewPager with the sections adapter.
        configureCategoryPager();
//
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.studiesTabs);
        tabLayout.setupWithViewPager(mViewPager);

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        observer = new FlowContentObserver();
        observer.registerForContentChanges(getContext(), Category.class);
    }



    @Override
    public void onDetach() {
        super.onDetach();


//        Handler handler = new Handler(getContext().getMainLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                // if you dont set the adapter to null then the object will not be garbage collected
//                mViewPager.setAdapter(null);
//                mViewPager = null;
//            }
//        });

        mSectionsPagerAdapter = null;

        observer.unregisterForContentChanges(getContext());
        observer = null;
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

            final GridView gridView = (GridView) rootView.findViewById(R.id.studiesGridView);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("Item", "onItemClick: " + position + studies.get(position).title);
                    Study study = studies.get(position);
                    APIManager.getInstance().downloadLessons(study.id);

                    ((BackstackDelegate)ServiceLocator.getService(getContext(), MainActivity.StackType.STUDIES.name())).getBackstack().goTo(StudyKey.create(studies.get(position).id));

                }
            });
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

//            float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

            gridView.setNumColumns((int)(dpWidth / 120));
            gridView.setAdapter(new BibleStudiesAdapter(getContext(), studies));

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
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
            // Show 3 total pages.
            return categories.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Category category = categories.get(position);
            return category.name;
        }
    }

    public static class BibleStudiesAdapter extends BaseAdapter {

        private final List<Study> mStudies;
        private final LayoutInflater mInflater;

        private Context mContext;

        public BibleStudiesAdapter(Context context, List<Study> studies) {
            mInflater = LayoutInflater.from(context);
            mStudies = studies;
            mContext = context;
        }

        @Override
        public int getCount() {
            return mStudies.size();
        }

        @Override
        public Study getItem(int position) {
            return mStudies.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                v = mInflater.inflate(R.layout.grid_bible_study_item, parent, false);
                v.setTag(R.id.bible_study_image_view, v.findViewById(R.id.bible_study_image_view));
            }

            ImageView picture = (ImageView) v.getTag(R.id.bible_study_image_view);

            Study study = mStudies.get(position);


            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

            int imageWidth = displayMetrics.widthPixels / 3;
            String studyImageURL = study.imageForWidth(imageWidth);
            if (studyImageURL != null) {
                Glide.with(mContext).load(studyImageURL).into(picture);
            } else {
                Glide.with(mContext).load(study.thumbnailSource).into(picture);
            }


            return v;
        }
    }

}
