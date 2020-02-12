package com.erpdevelopment.vbvm.fragments.media;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.api.APIManager;
import com.erpdevelopment.vbvm.application.MainActivity;
import com.erpdevelopment.vbvm.fragments.media.groupStudies.GroupStudiesFragment;
import com.erpdevelopment.vbvm.fragments.media.videos.VideoSeriesFragment;
import com.erpdevelopment.vbvm.fragments.shared.AbstractFragment;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomascarey on 9/09/17.
 */

public class MediaFragment extends AbstractFragment {
    private static final String TAG = "MediaFragment";

    @BindView(R.id.studiesContainer)
    ViewPager mViewPager;

    @BindView(R.id.studiesToolar)
    Toolbar toolbar;

    @BindView(R.id.studiesTabs)
    TabLayout tabLayout;

    private static Date lastRequestDate = null;

    public MediaFragment() {

    }

    public static MediaFragment newInstance() {
        MediaFragment fragment = new MediaFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media, container, false);
        unbinder = ButterKnife.bind(this, view);

        MainActivity.get(getContext()).setSupportActionBar(toolbar);
        MainActivity.get(getContext()).getSupportActionBar().setTitle(R.string.title_media);


        if(lastRequestDate == null || TimeUnit.MILLISECONDS.toSeconds((new Date()).getTime() - lastRequestDate.getTime()) > 3600 ) {
            lastRequestDate = new Date();
            APIManager.getInstance().downloadChannels(null);
            APIManager.getInstance().downloadGroupStudies(null);
        }

        SectionsPagerAdapter pagerAdapter = new SectionsPagerAdapter(this.getChildFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);
        return view;
    }

    @Override
    public boolean shouldBitmapUI() {
        return true;
    }

    private enum Media {
        STUDIES,
        VIDEOS
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private List<Media> mediaList = Arrays.asList(Media.STUDIES, Media.VIDEOS);

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            Media media = mediaList.get(position);

            switch (media) {
                case STUDIES:
                    return GroupStudiesFragment.newInstance();
                case VIDEOS:
                    return VideoSeriesFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mediaList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Media media = mediaList.get(position);
            switch (media) {
                case STUDIES:
                    return "Group Studies";
                case VIDEOS:
                    return "Video";
                default:
                    return "";
            }
        }
    }
}
