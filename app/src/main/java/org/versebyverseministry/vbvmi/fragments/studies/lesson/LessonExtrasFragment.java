package org.versebyverseministry.vbvmi.fragments.studies.lesson;


import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.versebyverseministry.vbvmi.FileHelpers;
import org.versebyverseministry.vbvmi.FontManager;
import org.versebyverseministry.vbvmi.GenericFileProvider;
import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.model.Lesson;
import org.versebyverseministry.vbvmi.model.Lesson_Table;
import org.versebyverseministry.vbvmi.model.Study;
import org.versebyverseministry.vbvmi.model.Study_Table;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private DownloadManager dm;
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
        dm = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

        getContext().registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
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
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(lesson.videoSource));
                    startActivity(i);
                });
            }));
        }

        if (FileHelpers.sourceForType(lesson, FileHelpers.FILE_AUDIO) != null) {
            rowList.add(new LessonExtrasAdapter.ActionRow(holder -> {
                holder.titleView.setText("Play audio");
                configureHolderIcon(holder, FileHelpers.FILE_AUDIO, () -> {
                    Intent intent = new Intent(getContext(), LessonAudioActivity.class);
                    intent.putExtra(LessonAudioActivity.ARG_LESSON_ID, lessonId);
                    intent.putExtra(LessonAudioActivity.ARG_START_AUDIO, true);
                    getContext().startActivity(intent);
                    dismiss();
                });
            }));
        }

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

    private interface SuccessCallback {
        void complete();
    }

    private void openFile(String type) {
        File file = FileHelpers.fileForType(getContext(), lesson, type);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri fileUrl = GenericFileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".org.versebyverseministry.vbvmi.GenericFileProvider", file);
        intent.setDataAndType(fileUrl, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
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

        final DownloadCallback callback = new DownloadCallback() {
            @Override
            public void downloadComplete(boolean success, String fileUri) {
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

        if (downloadIdsByType.containsKey(type)) {
            holder.iconView.setText(R.string.fa_download);
            holder.iconView.startAnimation(fadeOut);
            download(lesson, type, callback);
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
            download(lesson, type, callback);
        });
    }

    private interface DownloadCallback {
        void downloadComplete(boolean success, String fileUri);
    }

    private Map<Long, DownloadCallback> queuedCompletions = new HashMap<>();

    private Map<String, Long> downloadIdsByType = new HashMap<>();

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                // Download was complete
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                if (!queuedCompletions.containsKey(downloadId)) {
                    // this is not the download you're looking for
                    return;
                }
                DownloadCallback callback = queuedCompletions.remove(downloadId);
                Cursor c = dm.query(query);

                if (c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    switch (c.getInt(columnIndex)) {
                        case DownloadManager.STATUS_SUCCESSFUL: {

                            int fileUriIndex = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                            String fileUri = c.getString(fileUriIndex);

                            callback.downloadComplete(true, fileUri);
                            return;
                        }
                        case DownloadManager.STATUS_FAILED: {
                            dm.remove(downloadId);
                            break;
                        }
                        default:
                            break;
                    }
                }

                callback.downloadComplete(false, null);

            }
        }
    };


    private void download(Lesson lesson, String type, DownloadCallback callback) {
        String source = FileHelpers.sourceForType(lesson, type);
        if (source == null || callback == null || source.isEmpty()) {
            return;
        }
        DownloadCallback myCallback = new DownloadCallback() {
            @Override
            public void downloadComplete(boolean success, String fileUri) {
                if (downloadIdsByType.containsKey(type))
                    downloadIdsByType.remove(type);

                callback.downloadComplete(success, fileUri);
            }
        };

        if (downloadIdsByType.containsKey(type)) {
            long downloadId = downloadIdsByType.get(type);
            queuedCompletions.put(downloadId, myCallback);
            return;
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(source));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        request.setDestinationInExternalFilesDir(getContext(), "Documents", FileHelpers.relativePath(lesson, type));
        long downloadId = dm.enqueue(request);
        queuedCompletions.put(downloadId, myCallback);
        downloadIdsByType.put(type, downloadId);
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (downloadReceiver != null) {
            getContext().unregisterReceiver(downloadReceiver);
            downloadReceiver = null;
        }
        super.onDestroyView();
    }
}
