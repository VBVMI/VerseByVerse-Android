package com.erpdevelopment.vbvm.fragments.studies.lesson;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.erpdevelopment.vbvm.model.Study;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import com.erpdevelopment.vbvm.FileHelpers;
import com.erpdevelopment.vbvm.FontManager;
import com.erpdevelopment.vbvm.GenericFileProvider;
import com.erpdevelopment.vbvm.LessonResourceManager;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.Lesson_Table;
import com.erpdevelopment.vbvm.model.Study_Table;

import java.io.File;
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
    private static String TAG = "LessonExtras";

    private static String ARG_LESSON_ID = "ARG_LESSON_ID";
    private static String ARG_STUDY_ID = "ARG_STUDY_ID";

    private String studyId;
    private String lessonId;

    private Lesson lesson;
    private Study study;

    private Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;


    private Typeface iconFont;

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

        iconFont = FontManager.getTypeface(getContext(), FontManager.FONTAWESOME);
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
        View view = inflater.inflate(R.layout.dialog_lesson_extras, container, false);

        unbinder = ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        LessonExtrasAdapter.HeaderRow headerRow = new LessonExtrasAdapter.HeaderRow(lesson, study, getContext());

        List<LessonExtrasAdapter.Row> rowList = new ArrayList<>();
        rowList.add(headerRow);

        // Handout

        if (FileHelpers.sourceForType(lesson, FileHelpers.FILE_TRANSCRIPT) != null) {
            rowList.add(new LessonExtrasAdapter.ActionRow(holder -> {
                holder.titleView.setText("View transcript");
                configureHolderIcon(holder, FileHelpers.FILE_TRANSCRIPT, () -> {
                    Log.d(TAG, "Transcript complete");
                    openFile(FileHelpers.FILE_TRANSCRIPT);
                });
            }));
        }


        if (FileHelpers.sourceForType(lesson, FileHelpers.FILE_HANDOUT) != null) {
            rowList.add(new LessonExtrasAdapter.ActionRow(holder -> {
                holder.titleView.setText("View handout");
                configureHolderIcon(holder, FileHelpers.FILE_HANDOUT, () -> {
                    Log.d(TAG, "Handout complete");
                    openFile(FileHelpers.FILE_HANDOUT);
                });
            }));
        }

        if (FileHelpers.sourceForType(lesson, FileHelpers.FILE_SLIDES) != null) {
            rowList.add(new LessonExtrasAdapter.ActionRow(holder -> {
                holder.titleView.setText("View slides");
                configureHolderIcon(holder, FileHelpers.FILE_SLIDES, () -> {
                    Log.d(TAG, "Slides complete");
                    openFile(FileHelpers.FILE_SLIDES);
                });
            }));
        }

        if (lesson.videoSource != null && !lesson.videoSource.isEmpty()) {
            rowList.add(new LessonExtrasAdapter.ActionRow(holder -> {
                holder.titleView.setText("Watch video");
                holder.iconView.setTypeface(iconFont);
                holder.iconView.setText(R.string.fa_youtube_play);
                holder.itemView.setOnClickListener(v -> {
                    if (lesson.videoSource.contains("vimeo")) {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                        Uri data = Uri.parse(lesson.videoSource);
                        intent.setData(data);
                        startActivity(intent);
                    } else {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(lesson.videoSource));
                        startActivity(i);
                    }

                });
            }));
        }

        if (FileHelpers.sourceForType(lesson, FileHelpers.FILE_AUDIO) != null) {
            rowList.add(new LessonExtrasAdapter.ActionRow(holder -> {
                holder.titleView.setText("Play audio");
                configureHolderIcon(holder, FileHelpers.FILE_AUDIO, () -> {
                    if (isDetached() || !isAdded()) {
                        return;
                    }
                    Intent intent = new Intent(getContext(), LessonAudioActivity.class);
                    intent.putExtra(LessonAudioActivity.ARG_LESSON_ID, lessonId);
                    intent.putExtra(LessonAudioActivity.ARG_START_AUDIO, true);
                    getContext().startActivity(intent);
                    if (isStateSaved()) {
                        return;
                    }
                    dismiss();
                });
            }));
        }

        // Handout
        rowList.add(new LessonExtrasAdapter.ActionRow(holder -> {
            holder.titleView.setText(R.string.mark_complete);

            holder.iconView.setTypeface(iconFont);
            if (!lesson.complete) {
                holder.iconView.setText(R.string.fa_square_o);
            } else {
                holder.iconView.setText(R.string.fa_check_square_o);
            }

            holder.itemView.setOnClickListener(v -> {
                lesson.complete = !lesson.complete;
                if (!lesson.complete) {
                    holder.iconView.setText(R.string.fa_square_o);
                } else {
                    holder.iconView.setText(R.string.fa_check_square_o);
                }
                lesson.save();

                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Lesson.updated()));
            });
        }));

        LessonExtrasAdapter adapter = new LessonExtrasAdapter(rowList);

        if (FileHelpers.hasDownloadedFiles(getContext(), lesson)) {
            LessonExtrasAdapter.ActionRow row = new LessonExtrasAdapter.ActionRow(holder -> {
                holder.titleView.setText("Delete files");
                holder.iconView.setTypeface(iconFont);
                holder.iconView.setText(R.string.fa_trash_o);

                holder.itemView.setOnClickListener(v -> {
                    if (FileHelpers.deleteAllFiles(getContext(), lesson)) {
                        Snackbar snackbar = Snackbar.make(view, R.string.files_deleted, BaseTransientBottomBar.LENGTH_SHORT);
                        snackbar.show();
                        lesson.save();
                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Lesson.updated()));
                    } else {
                        Snackbar snackbar = Snackbar.make(view, R.string.files_deleted_failed, BaseTransientBottomBar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
            });
            rowList.add(row);
        }

        recyclerView.setAdapter(adapter);

        return view;
    }

    private interface SuccessCallback {
        void complete();
    }

    private void openFile(String type) {
        if (getContext() == null) {
            return;
        }
        File file = FileHelpers.fileForType(getContext(), lesson, type);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri fileUrl = GenericFileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".com.erpdevelopment.vbvm.GenericFileProvider", file);
        intent.setDataAndType(fileUrl, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.d(TAG, "openFile: Couldn't open file");
            Snackbar snackbar = Snackbar.make(getView(), R.string.no_pdf_reader, BaseTransientBottomBar.LENGTH_LONG);
            snackbar.show();
        }

    }

    private int iconIdForType(String type) {
        switch (type) {
            case FileHelpers.FILE_AUDIO:
                return R.string.fa_play;
            case FileHelpers.FILE_HANDOUT:
                return R.string.fa_file_o;
            case FileHelpers.FILE_SLIDES:
                return R.string.fa_file_powerpoint_o;
            case FileHelpers.FILE_TRANSCRIPT:
                return R.string.fa_file_text_o;
        }
        return 0;
    }

    private void configureHolderIcon(LessonExtrasAdapter.ActionViewHolder holder, String type, SuccessCallback successCallback) {
        holder.iconView.setTypeface(iconFont);

        final int textId = iconIdForType(type);

        final LessonResourceManager.DownloadCallback callback = new LessonResourceManager.DownloadCallback() {
            @Override
            public void downloadComplete(boolean success, String fileUri) {
                if (isDetached()) {
                    return;
                }
                holder.iconView.clearAnimation();
                holder.iconView.setText(textId);
                if (success) {
                    successCallback.complete();
                }
            }
        };

        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.3f);

        fadeOut.setDuration(500);
        fadeOut.setRepeatMode(Animation.REVERSE);
        fadeOut.setRepeatCount(Animation.INFINITE);


        if (LessonResourceManager.getInstance().isDownloadingResource(lessonId, type)) {
            holder.iconView.setText(R.string.fa_download);
            holder.iconView.startAnimation(fadeOut);
            LessonResourceManager.getInstance().setDownloadCallback(lessonId, type, callback);
        } else {
            holder.iconView.setText(textId);
        }

        holder.itemView.setOnClickListener(v -> {
            File file = FileHelpers.fileForType(getContext(), lesson, type);
            if (file.exists()) {
                successCallback.complete();
                return;
            }

            holder.iconView.setText(R.string.fa_download);
            holder.iconView.startAnimation(fadeOut);
            LessonResourceManager.getInstance().download(lesson, type, callback);
        });
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }
}
