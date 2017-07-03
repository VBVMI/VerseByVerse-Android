package org.versebyverseministry.vbvmi;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.versebyverseministry.vbvmi.model.Category;
import org.versebyverseministry.vbvmi.model.Category_Table;

import android.os.Handler;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudiesView.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StudiesView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudiesView extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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
    private ViewPager mViewPager;

    private FlowContentObserver observer;

    public StudiesView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudiesView.
     */
    // TODO: Rename and change types and number of parameters
    public static StudiesView newInstance(String param1, String param2) {
        StudiesView fragment = new StudiesView();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void configureCategoryPager() {
        List<Category> categories = SQLite.select().from(Category.class).orderBy(Category_Table.order, true).queryList();
        mSectionsPagerAdapter = new SectionsPagerAdapter(this.getFragmentManager(), categories);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_studies_view, container, false);

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
        mViewPager = (ViewPager) view.findViewById(R.id.studiesContainer);
        configureCategoryPager();
//
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.studiesTabs);
        tabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        observer = new FlowContentObserver();
        observer.registerForContentChanges(getContext(), Category.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;


        Handler handler = new Handler(getContext().getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // if you dont set the adapter to null then the object will not be garbage collected
                mViewPager.setAdapter(null);
                mViewPager = null;
            }
        });

        mSectionsPagerAdapter = null;

        observer.unregisterForContentChanges(getContext());
        observer = null;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_studies, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
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
            return PlaceholderFragment.newInstance(position + 1);
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
}
