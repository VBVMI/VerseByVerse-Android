package com.erpdevelopment.vbvm.fragments.media.videos;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erpdevelopment.vbvm.application.MainActivity;
import com.erpdevelopment.vbvm.fragments.groupStudy.VideoKey;
import com.erpdevelopment.vbvm.fragments.topics.AbstractListFragment;
import org.versebyverseministry.models.Channel;
import com.erpdevelopment.vbvm.util.ServiceLocator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zhuinden.simplestack.BackstackDelegate;

import org.algi.sugarloader.SugarLoader;

import java.util.List;

/**
 * Created by thomascarey on 23/09/17.
 */

public class VideoSeriesFragment extends AbstractListFragment implements VideoSeriesSelectionListener {

    VideoSeriesRecylcerAdapter adapter;

    private SugarLoader<List<Channel>> mLoader = new SugarLoader<List<Channel>>("VideoSeriesFragment")
            .background(() -> {
                return SQLite.select().from(Channel.class).queryList();
            }).onSuccess(channels -> {
                adapter.setVideoSeries(channels);
                if (channels.size() > 0) {
                    showList();
                } else {
                    showEmpty();
                }
            });

    public VideoSeriesFragment() {

    }

    public static VideoSeriesFragment newInstance() {
        return new VideoSeriesFragment();
    }

    @Override
    public void didSelectVideoSeries(Channel channel) {
        ((BackstackDelegate) ServiceLocator.getService(getContext(), MainActivity.StackType.MEDIA.name())).getBackstack().goTo(VideoKey.create(channel.id));
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoader.init(this);
    }

    @Override
    protected String tableName() {
        return Channel.updated();
    }

    @Override
    protected void configureRecyclerView(RecyclerView recyclerView) {
        adapter = new VideoSeriesRecylcerAdapter(this);
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
}
