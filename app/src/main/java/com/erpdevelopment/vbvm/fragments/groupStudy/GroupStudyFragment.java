package com.erpdevelopment.vbvm.fragments.groupStudy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.StringHelpers;
import com.erpdevelopment.vbvm.application.MainActivity;
import com.erpdevelopment.vbvm.fragments.topics.AbstractListFragment;
import com.erpdevelopment.vbvm.model.GroupStudy;
import com.erpdevelopment.vbvm.model.GroupStudy_Table;
import com.erpdevelopment.vbvm.model.Video;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.vimeo.networking.VimeoClient;
import com.vimeo.networking.callbacks.ModelCallback;
import com.vimeo.networking.model.VideoFile;
import com.vimeo.networking.model.error.VimeoError;
import com.vimeo.networking.model.playback.Play;

import org.algi.sugarloader.SugarLoader;

import java.util.ArrayList;

import butterknife.BindView;


/**
 * Created by thomascarey on 11/09/17.
 */

public class GroupStudyFragment extends AbstractListFragment implements VideoSelectionListener {
    private static final String ARG_GROUP_STUDY_ID = "ARG_GROUP_STUDY_ID";
    private static final String TAG = "GroupStudyFragment";

    private String groupStudyId;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private GroupStudyRecyclerAdapter adapter;

    public GroupStudyFragment() {

    }

    private SugarLoader<GroupStudy> mLoader = new SugarLoader<GroupStudy>("GroupStudyFragment")
            .background(() -> {
                GroupStudy groupStudy =  SQLite.select().from(GroupStudy.class).where(GroupStudy_Table.id.eq(groupStudyId)).querySingle();
                if (groupStudy != null) {
                    groupStudy.fetchVideos();
                }
                return groupStudy;
            }).onSuccess(this::configureForGroupStudy);

    public static GroupStudyFragment newInstance(String groupStudyId) {
        GroupStudyFragment fragment = new GroupStudyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_STUDY_ID, groupStudyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupStudyId = getArguments().getString(ARG_GROUP_STUDY_ID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoader.init(this);
    }

    @Override
    protected String tableName() {
        return "Video";
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_group_study;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        MainActivity.get(getContext()).setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        MainActivity.get(getContext()).getSupportActionBar().setTitle("");
        //noinspection ConstantConditions
        MainActivity.get(getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.get(getContext()).getMultistack().onBackPressed();
            }
        });

        return view;
    }

    @Override
    protected void configureRecyclerView(RecyclerView recyclerView) {
        adapter = new GroupStudyRecyclerAdapter(this, getContext());
        if (adapter.getItemCount() > 0) {
            showList();
        } else {
            showLoading();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void reloadData() {
        mLoader.restart(this);
    }

    private void configureForGroupStudy(GroupStudy groupStudy) {
        adapter.setGroupStudy(groupStudy);
        showList();
        //noinspection ConstantConditions
        MainActivity.get(getContext()).getSupportActionBar().setTitle(StringHelpers.fromHtmlString(groupStudy.title));
    }

    @Override
    public void videoTapped(Video video) {

        if (video.service.equals("vimeo")) {

            String uri = "videos/" + video.serviceVideoId;
            VimeoClient.getInstance().fetchNetworkContent(uri, new ModelCallback<com.vimeo.networking.model.Video>(com.vimeo.networking.model.Video.class) {
                @Override
                public void success(com.vimeo.networking.model.Video video) {

                    for (VideoFile file : video.files) {
                        if (file.getQuality().equals(VideoFile.VideoQuality.HLS)) {

                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                            Uri data = Uri.parse(file.getLink());
                            intent.setDataAndType(data, "video/mp4");
                            startActivity(intent);

                        }
                    }
                }

                @Override
                public void failure(VimeoError error) {
                    Log.e(TAG, "Error loading video " + error.toString());
                }
            });


        } else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.videoSource));
            getContext().startActivity(browserIntent);
        }

    }
}
