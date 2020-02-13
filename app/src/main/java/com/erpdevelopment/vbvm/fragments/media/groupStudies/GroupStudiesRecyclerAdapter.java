package com.erpdevelopment.vbvm.fragments.media.groupStudies;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.StringHelpers;
import com.erpdevelopment.vbvm.model.GroupStudy;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by thomascarey on 10/09/17.
 */

public class GroupStudiesRecyclerAdapter extends RecyclerView.Adapter<GroupStudiesRecyclerAdapter.GroupStudyViewHolder> {

    private List<GroupStudy> groupStudies;
    private GroupStudySelectionListener groupStudySelectionListener;
    private Context context;

    GroupStudiesRecyclerAdapter(GroupStudySelectionListener groupStudySelectionListener, Context context) {
        this.groupStudies = new ArrayList<>();
        this.groupStudySelectionListener = groupStudySelectionListener;
        this.context = context;
    }

    public void setGroupStudies(List<GroupStudy> groupStudies) {
        this.groupStudies = groupStudies;
        this.notifyDataSetChanged();
    }

    @Override
    public GroupStudyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_study, parent, false);
        return new GroupStudyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupStudyViewHolder holder, int position) {

        GroupStudy g = groupStudies.get(position);

        holder.titleView.setText(StringHelpers.fromHtmlString(g.title));

        holder.countTextView.setText("" + g.videoCount + " videos");

        Date date = new Date(TimeUnit.MILLISECONDS.convert(g.postedDate, TimeUnit.SECONDS));
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

        holder.dateView.setText(dateFormat.format(date));

        Glide.with(context).load(g.coverImage).into(holder.imageView);

        holder.background.setOnClickListener(v -> {
            if (groupStudySelectionListener != null) {
                groupStudySelectionListener.didSelectGroupStudy(g);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupStudies.size();
    }

    class GroupStudyViewHolder extends RecyclerView.ViewHolder {

        View background;
        TextView titleView;
        ImageView imageView;
        TextView countTextView;
        TextView dateView;

        GroupStudyViewHolder(View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.background);
            titleView = itemView.findViewById(R.id.title_view);
            imageView = itemView.findViewById(R.id.poster_image_view);
            countTextView = itemView.findViewById(R.id.count_text_view);
            dateView = itemView.findViewById(R.id.date_view);
        }
    }

}
