package org.versebyverseministry.vbvmi.fragments.studies.lesson;


import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.versebyverseministry.vbvmi.FontManager;
import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.model.Lesson;
import org.versebyverseministry.vbvmi.model.Lesson_Table;
import org.versebyverseministry.vbvmi.model.Study;
import org.versebyverseministry.vbvmi.model.Study_Table;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LessonExtrasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LessonExtrasFragment extends DialogFragment {

    private static String ARG_LESSON_ID = "ARG_LESSON_ID";
    private static String ARG_STUDY_ID = "ARG_STUDY_ID";

    private String studyId;
    private String lessonId;

    private Lesson lesson;
    private Study study;

    private Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public LessonExtrasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LessonExtrasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LessonExtrasFragment newInstance(String lessonId, String studyId) {
        LessonExtrasFragment fragment = new LessonExtrasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LESSON_ID, lessonId);
        args.putString(ARG_STUDY_ID, studyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            studyId = getArguments().getString(ARG_STUDY_ID);
            lessonId = getArguments().getString(ARG_LESSON_ID);

            study = SQLite.select().from(Study.class).where(Study_Table.id.eq(studyId)).querySingle();

            lesson = SQLite.select().from(Lesson.class).where(Lesson_Table.id.eq(lessonId)).querySingle();
        }

        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_lesson_extras, container, false);





        unbinder = ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        LessonExtrasAdapter.HeaderRow headerRow = new LessonExtrasAdapter.HeaderRow(lesson, study, getContext());

        List<LessonExtrasAdapter.Row> rowList = new ArrayList<>();
        rowList.add(headerRow);

        Typeface iconFont = FontManager.getTypeface(getContext(), FontManager.FONTAWESOME);

        // Handout
        rowList.add(new LessonExtrasAdapter.ActionRow(holder -> {
            holder.titleView.setText("View transcript");
            holder.iconView.setTypeface(iconFont);
            holder.iconView.setText(R.string.fa_file_text_o);
        }));

        // Handout
        rowList.add(new LessonExtrasAdapter.ActionRow(holder -> {
            holder.titleView.setText("View handout");
            holder.iconView.setTypeface(iconFont);
            holder.iconView.setText(R.string.fa_file_o);
        }));

        // Handout
        rowList.add(new LessonExtrasAdapter.ActionRow(holder -> {
            holder.titleView.setText("View slides");
            holder.iconView.setTypeface(iconFont);
            holder.iconView.setText(R.string.fa_file_powerpoint_o);
        }));

        // Handout
        rowList.add(new LessonExtrasAdapter.ActionRow(holder -> {
            holder.titleView.setText("Watch video");
            holder.iconView.setTypeface(iconFont);
            holder.iconView.setText(R.string.fa_youtube_play);
        }));

        // Handout
        rowList.add(new LessonExtrasAdapter.ActionRow(holder -> {
            holder.titleView.setText("Play audio");
            holder.iconView.setTypeface(iconFont);
            holder.iconView.setText(R.string.fa_play);
        }));

        // Handout
        rowList.add(new LessonExtrasAdapter.ActionRow(holder -> {
            holder.titleView.setText("Mark as complete");
            holder.iconView.setTypeface(iconFont);
            holder.iconView.setText(R.string.fa_check_square_o);
        }));

        LessonExtrasAdapter adapter = new LessonExtrasAdapter(rowList);




        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        super.onDestroyView();
    }
}
