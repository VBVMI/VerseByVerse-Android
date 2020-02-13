package com.erpdevelopment.vbvm.fragments.studies.lesson;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
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

/**
 * Created by thomascarey on 16/08/17.
 */



public class LessonExtrasAdapter extends RecyclerView.Adapter<ViewHolder> {

    static abstract class Row<U extends ViewHolder> {
        abstract ViewType getViewType();

        abstract void bindViewHolder(U holder);
    }

    static class HeaderRow extends LessonExtrasAdapter.Row<HeaderViewHolder> {
        private Lesson lesson;

        private Study study;
        private Context context;

        HeaderRow(Lesson lesson, Study study, Context context) {
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

    static class ActionRow extends LessonExtrasAdapter.Row<ActionViewHolder> {

        public interface Binder {
            void bindViewHolder(ActionViewHolder holder);
        }

        private Binder binder;

        ActionRow(Binder binder) {
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

    LessonExtrasAdapter(List<Row> rows) {
        extrasRows = rows;
    }

    public void removeRow(Row row) {
        int index = extrasRows.indexOf(row);
        if (index > 0) {
            extrasRows.remove(row);
            notifyItemRemoved(index);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewType type = ViewType.fromId(viewType);
        assert type != null;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

    class HeaderViewHolder extends ViewHolder {

        ImageView imageView;
        TextView titleView;

        HeaderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            titleView = itemView.findViewById(R.id.title_view);
        }
    }

    class ActionViewHolder extends ViewHolder {

        TextView titleView;
        TextView iconView;

        ActionViewHolder(View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.icon_view);
            titleView = itemView.findViewById(R.id.title_view);
        }
    }

}
