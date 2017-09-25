package com.erpdevelopment.vbvm.fragments.studies.lesson;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.model.Lesson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thomascarey on 16/08/17.
 */



public class LessonExtrasAdapter extends RecyclerView.Adapter<ViewHolder> {

    public static abstract class Row<U extends ViewHolder> {
        abstract ViewType getViewType();

        abstract void bindViewHolder(U holder);
    }

    public static class HeaderRow extends LessonExtrasAdapter.Row<HeaderViewHolder> {
        private Lesson lesson;

        private Study study;
        private Context context;

        public HeaderRow(Lesson lesson, Study study, Context context) {
            this.lesson = lesson;
            this.study = study;
            this.context = context;
        }

        @Override
        LessonExtrasAdapter.ViewType getViewType() {
            return LessonExtrasAdapter.ViewType.HEADER;
        }

        @Override
        void bindViewHolder(HeaderViewHolder holder) {

            int imageWidth = (int) context.getResources().getDimension(R.dimen.study_extra_header_image_width);
            String studyImageURL = study.imageForWidth(imageWidth);
            if (studyImageURL != null) {
                Glide.with(context).load(studyImageURL).into(holder.imageView);
            } else {
                Glide.with(context).load(study.thumbnailSource).into(holder.imageView);
            }

            holder.titleView.setText(lesson.title);
        }
    }

    public static class ActionRow extends LessonExtrasAdapter.Row<ActionViewHolder> {

        public interface Binder {
            void bindViewHolder(ActionViewHolder holder);
        }

        private Binder binder;

        public ActionRow(Binder binder) {
            this.binder = binder;
        }

        @Override
        LessonExtrasAdapter.ViewType getViewType() {
            return LessonExtrasAdapter.ViewType.ACTION;
        }

        @Override
        void bindViewHolder(ActionViewHolder holder) {
            binder.bindViewHolder(holder);
        }
    }

    public enum ViewType {
        HEADER(0),
        ACTION(1);

        int id;

        ViewType(int id) {
            this.id = id;
        }

        static ViewType fromId(int id) {
            for(ViewType type : values()) {
                if (type.id == id) {
                    return type;
                }
            }
            return null;
        }
    }

    private List<Row> extrasRows;

    public LessonExtrasAdapter(List<Row> rows) {
        extrasRows = rows;
    }

    public void removeRow(Row row) {
        int index = extrasRows.indexOf(row);
        if (index > 0) {
            extrasRows.remove(row);
            notifyItemRemoved(index);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewType type = ViewType.fromId(viewType);
        switch (type) {
            case HEADER: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_extras_header, parent, false);
                return new HeaderViewHolder(view);
            }
            case ACTION: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_extras_action, parent, false);
                return new ActionViewHolder(view);
            }
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Row r = extrasRows.get(position);
        r.bindViewHolder(holder);
    }

    @Override
    public int getItemViewType(int position) {
        return extrasRows.get(position).getViewType().id;
    }

    @Override
    public int getItemCount() {
        return extrasRows.size();
    }

    public class HeaderViewHolder extends ViewHolder {

        @BindView(R.id.image_view)
        ImageView imageView;

        @BindView(R.id.title_view)
        TextView titleView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ActionViewHolder extends ViewHolder {

        @BindView(R.id.title_view)
        TextView titleView;

        @BindView(R.id.icon_view)
        TextView iconView;

        public ActionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
