package com.erpdevelopment.vbvm.fragments.topics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erpdevelopment.vbvm.views.EmptyView;
import com.raizlabs.android.dbflow.runtime.OnTableChangedListener;
import com.raizlabs.android.dbflow.structure.BaseModel;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.api.DatabaseManager;
import com.erpdevelopment.vbvm.views.LoadingView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by thomascarey on 27/08/17.
 */

public abstract class AbstractListFragment extends Fragment {

    protected Unbinder unbinder;

    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;

    @BindView(R.id.loading_view)
    protected LoadingView loadingView;

    @BindView(R.id.empty_view)
    protected EmptyView emptyView;

    private Handler mainHandler;

    private Handler getMainHandler() {
        return mainHandler;
    }
    protected abstract String tableName();

    private BroadcastReceiver modelUpdatedReceiver;

    protected abstract void configureRecyclerView(RecyclerView recyclerView);

    protected void showEmpty() {
        if(isDetached() || loadingView == null || recyclerView == null || emptyView == null) {
            return;
        }
        loadingView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    protected void showLoading() {
        if(isDetached() || loadingView == null || recyclerView == null || emptyView == null) {
            return;
        }
        loadingView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    protected void showList() {
        if(isDetached() || loadingView == null || recyclerView == null || emptyView == null) {
            return;
        }
        loadingView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    protected abstract void reloadData();

    protected int layoutId() {
        return R.layout.fragment_list;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        mainHandler = new Handler(getContext().getMainLooper());

        configureRecyclerView(recyclerView);

        modelUpdatedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mainHandler.post(() -> {
                    reloadData();
                });
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(modelUpdatedReceiver, new IntentFilter(tableName()));
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //DatabaseManager.observer.removeTableChangedListener(tableChangedListener);
        if (modelUpdatedReceiver != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(modelUpdatedReceiver);
            modelUpdatedReceiver = null;
        }
    }

    public void setSearchText(String searchText) {
    }
}
