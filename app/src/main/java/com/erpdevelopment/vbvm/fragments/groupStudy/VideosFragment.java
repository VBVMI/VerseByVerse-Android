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
import com.erpdevelopment.vbvm.model.Channel;
import com.erpdevelopment.vbvm.model.Channel_Table;
import com.erpdevelopment.vbvm.model.Video;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.vimeo.networking.VimeoClient;
import com.vimeo.networking.callbacks.ModelCallback;
import com.vimeo.networking.model.VideoFile;
import com.vimeo.networking.model.error.VimeoError;

import org.algi.sugarloader.SugarLoader;

import butterknife.BindView;

/**
 * Created by thomascarey on 23/09/17.
 */

public class VideosFragment extends AbstractListFragment implements VideoSelectionListener {

    private static final String ARG_CHANNEL_ID = "ARG_CHANNEL_ID";
    private static final String TAG = "VideosFragment";

    private String channelId;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private VideoRecyclerAdapter adapter;

    public VideosFragment() {

    }

    private SugarLoader<Channel> mLoader = new SugarLoader<Channel>("VideosFragment")
            .background(() -> {
                Channel channel =  SQLite.select().from(Channel.class).where(Channel_Table.id.eq(channelId)).querySingle();
                if (channel != null) {
                    channel.fetchVideos();
                }
                return channel;
            }).onSuccess(this::configureForChannel);



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

    public static VideosFragment newInstance(String channelId) {
        VideosFragment fragment = new VideosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHANNEL_ID, channelId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            channelId = getArguments().getString(ARG_CHANNEL_ID);
        }
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
        adapter = new VideoRecyclerAdapter(this, getContext());
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

    private void configureForChannel(Channel channel) {
        adapter.setChannel(channel);
        showList();
        //noinspection ConstantConditions
        MainActivity.get(getContext()).getSupportActionBar().setTitle(StringHelpers.fromHtmlString(channel.title));
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
