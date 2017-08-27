package org.versebyverseministry.vbvmi.fragments.answers;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.dbflow.runtime.OnTableChangedListener;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.api.DatabaseManager;
import org.versebyverseministry.vbvmi.fragments.shared.AbstractFragment;
import org.versebyverseministry.vbvmi.views.LoadingView;

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

    private Handler mainHandler;

    private Handler getMainHandler() {
        return mainHandler;
    }
    abstract String tableName();

    OnTableChangedListener tableChangedListener = new OnTableChangedListener() {
        @Override
        public void onTableChanged(@Nullable Class<?> tableChanged, @NonNull BaseModel.Action action) {
            if (tableChanged.toString().contains(tableName()) && getMainHandler() != null) {
                getMainHandler().post(() -> {
                   reloadData();
                });
            }
        }
    };

    protected abstract void configureRecyclerView(RecyclerView recyclerView);


    protected void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    protected void showList() {
        loadingView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    abstract void reloadData();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        mainHandler = new Handler(getContext().getMainLooper());

        configureRecyclerView(recyclerView);

        DatabaseManager.observer.addOnTableChangedListener(tableChangedListener);

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
        DatabaseManager.observer.removeTableChangedListener(tableChangedListener);
        tableChangedListener = null;
    }
}