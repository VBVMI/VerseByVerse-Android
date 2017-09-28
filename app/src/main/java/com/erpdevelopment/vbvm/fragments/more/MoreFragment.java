package com.erpdevelopment.vbvm.fragments.more;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.erpdevelopment.vbvm.FontManager;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.application.MainActivity;
import com.erpdevelopment.vbvm.fragments.shared.AbstractFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomascarey on 26/09/17.
 */

public class MoreFragment extends AbstractFragment {
    private static final String TAG = "MoreFragment";

    public MoreFragment() {

    }

    static MoreFragment newInstance() {
        return new MoreFragment();
    }

    @BindView(R.id.studiesToolar)
    Toolbar toolbar;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        unbinder = ButterKnife.bind(this, view);
        MainActivity.get(getContext()).setSupportActionBar(toolbar);
        MainActivity.get(getContext()).getSupportActionBar().setTitle(R.string.title_more);

        List<Item> items = new ArrayList<>();
        items.add(new Item("About", R.string.fa_info, "https://www.versebyverseministry.org/about"));
        items.add(new Item("Events", R.string.fa_calendar, "https://www.versebyverseministry.org/events"));
        items.add(new Item("Contact", R.string.fa_commenting_o, "https://www.versebyverseministry.org/contact"));
        items.add(new Item("Donate", R.string.fa_envelope_o, "https://www.versebyverseministry.org/about/financial_support"));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        MoreRecyclerAdapter adapter = new MoreRecyclerAdapter(items);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public boolean shouldBitmapUI() {
        return true;
    }

    static class Item {
        public String name;
        @StringRes
        public int icon;
        public String link;

        Item(String name, int icon, String link) {
            this.name = name; this.icon = icon; this.link = link;
        }
    }


    class MoreRecyclerAdapter extends RecyclerView.Adapter<MoreItemViewHolder> {

        List<Item> items;
        private Typeface iconFont;

        MoreRecyclerAdapter(List<Item> items) {
            this.items = items;
            iconFont = FontManager.getTypeface(getContext(), FontManager.FONTAWESOME);
        }

        @Override
        public MoreItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_more_item, parent, false);
            return new MoreItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MoreItemViewHolder holder, int position) {
            Item item = items.get(position);

            holder.iconView.setTypeface(iconFont);
            holder.iconView.setText(item.icon);

            holder.nameView.setText(item.name);

            holder.background.setOnClickListener(v -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(item.link));
                startActivity(i);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    class MoreItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.icon_view)
        TextView iconView;

        @BindView(R.id.name_view)
        TextView nameView;

        @BindView(R.id.background)
        View background;

        public MoreItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
