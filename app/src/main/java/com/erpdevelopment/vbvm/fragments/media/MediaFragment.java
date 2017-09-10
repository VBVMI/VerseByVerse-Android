package com.erpdevelopment.vbvm.fragments.media;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.api.APIManager;
import com.erpdevelopment.vbvm.application.MainActivity;
import com.erpdevelopment.vbvm.fragments.shared.AbstractFragment;

import java.util.Date;
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
            APIManager.getInstance().downloadChannels(success -> {
                Log.d(TAG, "Downloaded all them channels (" + success + ")");
            });
//            APIManager.getInstance().downloa(success -> {
//                Log.d(TAG, "Downloaded all them answers (" + success + ")");
//            });
        }

        return view;
    }

    @Override
    public boolean shouldBitmapUI() {
        return true;
    }
}
