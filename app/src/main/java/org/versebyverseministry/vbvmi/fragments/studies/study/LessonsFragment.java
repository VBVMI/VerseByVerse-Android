package org.versebyverseministry.vbvmi.fragments.studies.study;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.dbflow.runtime.OnTableChangedListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.algi.sugarloader.SugarLoader;
import org.algi.sugarloader.function.Consumer;
import org.algi.sugarloader.function.Supplier;
import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.api.DatabaseManager;
import org.versebyverseministry.vbvmi.fragments.shared.AbstractFragment;
import org.versebyverseministry.vbvmi.fragments.studies.lesson.LessonAudioActivity;
import org.versebyverseministry.vbvmi.model.Lesson;
import org.versebyverseministry.vbvmi.model.Lesson_Table;
import org.versebyverseministry.vbvmi.services.DownloadService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.http.Query;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnLessonFragmentInteractionListener}
 * interface.
 */
public class LessonsFragment extends AbstractFragment {

    private static final String TAG = "LESSONS_FRAGMENT";
    private static final String ARG_STUDY_ID = "ARG_STUDY_ID";

    private OnLessonFragmentInteractionListener mListener;

    private String studyId;

    private RecyclerView view;

    private SugarLoader<List<Lesson>> mLoader;

    private DownloadManager dm;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LessonsFragment() {
    }

    public static LessonsFragment newInstance(String studyId) {
        LessonsFragment fragment = new LessonsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STUDY_ID, studyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dm = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

        if (getArguments() != null) {
            studyId = getArguments().getString(ARG_STUDY_ID);
        }
    }

    OnTableChangedListener tableChangedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final RecyclerView view = (RecyclerView) inflater.inflate(R.layout.fragment_lesson_list, container, false);




        mListener = new OnLessonFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Lesson lesson) {
                onStartLesson(lesson);
            }
        };

        mLoader = new SugarLoader<List<Lesson>>("LessonsLoader")
                .background(() ->
                        SQLite.select().from(Lesson.class).where(Lesson_Table.studyId.eq(studyId)).orderBy(Lesson_Table.index, true).queryList()
                ).onSuccess(lessons -> {
                    MyLessonRecyclerViewAdapter adapter = (MyLessonRecyclerViewAdapter)view.getAdapter();
                    adapter.setLessons(lessons);
                });

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            this.view = recyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyLessonRecyclerViewAdapter(new ArrayList<>(), mListener));
        }

        final Handler mainHandler = new Handler(getContext().getMainLooper());

        tableChangedListener = new OnTableChangedListener() {
            @Override
            public void onTableChanged(@Nullable Class<?> tableChanged, @NonNull BaseModel.Action action) {
                Log.d(TAG, "Table Changed: " + tableChanged.toString());
                if(tableChanged.toString().contains("Lesson")) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            reloadData();
                        }
                    });
                }
            }
        };

        DatabaseManager.observer.addOnTableChangedListener(tableChangedListener);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mLoader.init(this);
    }

    public void reloadData() {
        mLoader.restart(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        DatabaseManager.observer.removeTableChangedListener(tableChangedListener);
        Log.d("LESSONFRAGMENT", "removed table change listener");
        tableChangedListener = null;
        mListener = null;
    }

    private long enqueue = 0;

    private String relativeAudioPath(Lesson lesson) {
        Uri audioSource = Uri.parse(lesson.audioSource);
        String audioName = audioSource.getLastPathSegment();
        return "lessons/" + audioName;
    }

    private Uri audioFilePathForLesson(Lesson lesson) {
        File directory = getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        Uri basePath = Uri.fromFile(directory);
        return Uri.withAppendedPath(basePath, relativeAudioPath(lesson));
    }

    private File getAudioFileForLesson(Lesson lesson) {
        File file = new File(audioFilePathForLesson(lesson).getPath());
        return file;
    }

    private BroadcastReceiver downloadReceiver;

    private void downloadLesson(Lesson lesson) {
        if (lesson.audioSource == null) {
            return;
        }

        cleanUpReceiver();
        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    // Download was complete
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    Cursor c = dm.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        switch (c.getInt(columnIndex)) {
                            case DownloadManager.STATUS_SUCCESSFUL: {
                                playAudio(lesson);
                                break;
                            }
                            case DownloadManager.STATUS_FAILED: {
                                dm.remove(enqueue);
                                break;
                            }
                            default:
                                return;
                        }
                    }
                    enqueue = 0;
                    cleanUpReceiver();
                }
            }
        };

        getContext().registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(lesson.audioSource));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationInExternalFilesDir(getContext(), Environment.DIRECTORY_DOCUMENTS, relativeAudioPath(lesson));
        enqueue = dm.enqueue(request);
    }

    private void cleanUpReceiver() {
        if (downloadReceiver != null) {
            getContext().unregisterReceiver(downloadReceiver);
            downloadReceiver = null;
        }
    }

    public void playAudio(Lesson lesson) {

//        Intent i = new Intent();
//        i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
//        startActivity(i);

        Intent intent = new Intent(getContext(), LessonAudioActivity.class);
        intent.putExtra(LessonAudioActivity.ARG_LESSON_ID, lesson.id);
        intent.putExtra(LessonAudioActivity.ARG_LESSON_PATH, audioFilePathForLesson(lesson).getPath());
        getContext().startActivity(intent);
        AppCompatActivity activity = (AppCompatActivity) getContext();
        activity.overridePendingTransition(R.anim.slide_up, R.anim.stay);
    }

    public void onStartLesson(Lesson lesson) {

        if (enqueue != 0) {
            dm.remove(enqueue);
            enqueue = 0;
        }

        File audio = getAudioFileForLesson(lesson);
        if (!audio.exists()) {
            // Download the lesson eh
            downloadLesson(lesson);
        } else {
            playAudio(lesson);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLessonFragmentInteractionListener {
        void onListFragmentInteraction(Lesson lesson);
    }
}
