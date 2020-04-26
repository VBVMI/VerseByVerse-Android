package com.erpdevelopment.vbvm.fragments.media.groupStudies;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erpdevelopment.vbvm.application.MainActivity;
import com.erpdevelopment.vbvm.fragments.groupStudy.GroupStudyKey;
import com.erpdevelopment.vbvm.fragments.topics.AbstractListFragment;
import org.versebyverseministry.models.GroupStudy;
import com.erpdevelopment.vbvm.util.ServiceLocator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zhuinden.simplestack.BackstackDelegate;

import org.algi.sugarloader.SugarLoader;

import java.util.List;

/**
 * Created by thomascarey on 10/09/17.
 */

public class GroupStudiesFragment extends AbstractListFragment implements  GroupStudySelectionListener {

    GroupStudiesRecyclerAdapter adapter;

    private SugarLoader<List<GroupStudy>> mLoader = new SugarLoader<List<GroupStudy>>("GroupStudyListFragment")
                .background(() -> {
                    return SQLite.select().from(GroupStudy.class).queryList();
                }).onSuccess(groupStudies -> {
               adapter.setGroupStudies(groupStudies);
                if (groupStudies.size() > 0) {
                    showList();
                } else {
                    showEmpty();
                }
            });

    public GroupStudiesFragment() {

    }

    public static GroupStudiesFragment newInstance() {
        return new GroupStudiesFragment();
    }


    @Override
    public void didSelectGroupStudy(GroupStudy groupStudy) {
        ((BackstackDelegate) ServiceLocator.getService(getContext(), MainActivity.StackType.MEDIA.name())).getBackstack().goTo(GroupStudyKey.create(groupStudy.id));
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoader.init(this);
    }

    @Override
    protected String tableName() {
        return GroupStudy.updated();
    }

    @Override
    protected void configureRecyclerView(RecyclerView recyclerView) {
        adapter = new GroupStudiesRecyclerAdapter(this, getContext());
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
