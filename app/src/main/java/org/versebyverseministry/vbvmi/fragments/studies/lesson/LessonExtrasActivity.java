package org.versebyverseministry.vbvmi.fragments.studies.lesson;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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

public class LessonExtrasActivity extends AppCompatActivity {

    public static String ARG_LESSON_ID = "ARG_LESSON_ID";
    public static String ARG_STUDY_ID = "ARG_STUDY_ID";

    private Study study;

    private Lesson lesson;


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_extras);

        ButterKnife.bind(this);

        String studyId = getIntent().getExtras().getString(ARG_STUDY_ID);
        study = SQLite.select().from(Study.class).where(Study_Table.id.eq(studyId)).querySingle();

        String lessonId = getIntent().getExtras().getString(ARG_LESSON_ID);
        lesson = SQLite.select().from(Lesson.class).where(Lesson_Table.id.eq(lessonId)).querySingle();

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        LessonExtrasAdapter.HeaderRow headerRow = new LessonExtrasAdapter.HeaderRow(lesson, study, this);

        List<LessonExtrasAdapter.Row> rowList = new ArrayList<>();
        rowList.add(headerRow);

        Typeface iconFont = FontManager.getTypeface(this, FontManager.FONTAWESOME);

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

    }









}
