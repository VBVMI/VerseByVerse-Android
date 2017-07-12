package org.versebyverseministry.vbvmi.fragments.studies;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
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
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.KeyContextWrapper;

import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.model.Category;
import org.versebyverseministry.vbvmi.model.Category$$Parcelable;
import org.versebyverseministry.vbvmi.model.Category_Table;
import org.versebyverseministry.vbvmi.model.Study;
import org.versebyverseministry.vbvmi.model.Study_Table;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomascarey on 12/07/17.
 */

public class StudiesView extends LinearLayout {
    public StudiesView(Context context) {
        super(context);
    }

    public StudiesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StudiesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public StudiesView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context) {
        if (!isInEditMode()) {
            studiesKey = Backstack.getKey(context);
        }
    }

    StudiesKey studiesKey;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.studiesToolar)
    Toolbar toolbar;

    @BindView(R.id.studiesTabs)
    TabLayout tabLayout;

    @BindView(R.id.studiesContainer)
    ViewPager viewPager;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);

        //configureCategoryPager();
        //tabLayout.setupWithViewPager(viewPager);
    }

    private void configureCategoryPager() {
        List<Category> categories = SQLite.select().from(Category.class).orderBy(Category_Table.order, true).queryList();
        AppCompatActivity activity = (AppCompatActivity) ((KeyContextWrapper)getContext()).getBaseContext();
        mSectionsPagerAdapter = new SectionsPagerAdapter(activity.getSupportFragmentManager(), categories);
        viewPager.setAdapter(mSectionsPagerAdapter);
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
