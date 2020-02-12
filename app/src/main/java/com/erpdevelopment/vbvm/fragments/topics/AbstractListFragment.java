package com.erpdevelopment.vbvm.fragments.topics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erpdevelopment.vbvm.views.EmptyView;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.views.LoadingView;

/**
 * Created by thomascarey on 27/08/17.
 */

public abstract class AbstractListFragment extends Fragment {

    protected RecyclerView recyclerView;

    protected LoadingView loadingView;

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
        recyclerView = view.findViewById(R.id.recycler_view);
        loadingView = view.findViewById(R.id.loading_view);
        emptyView = view.findViewById(R.id.empty_view);

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
