package com.erpdevelopment.vbvm.fragments.studies.study;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erpdevelopment.vbvm.LessonResourceManager;
import com.erpdevelopment.vbvm.api.DatabaseManager;
import com.erpdevelopment.vbvm.fragments.shared.AbstractFragment;
import com.erpdevelopment.vbvm.fragments.studies.lesson.LessonExtrasFragment;
import com.erpdevelopment.vbvm.fragments.studies.lesson.LessonRecyclerViewAdapter;
import com.erpdevelopment.vbvm.views.LoadingView;
import com.raizlabs.android.dbflow.runtime.OnTableChangedListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.algi.sugarloader.SugarLoader;
import com.erpdevelopment.vbvm.FileHelpers;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.fragments.studies.lesson.LessonAudioActivity;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.Lesson_Table;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnLessonFragmentInteractionListener}
 * interface.
 */
public class LessonsFragment extends AbstractFragment {

    private static final String TAG = "LESSONS_FRAGMENT";
    private static final String ARG_STUDY_ID = "ARG_STUDY_ID";
    private static final String ARG_SHOW_COMPLETED = "ARG_SHOW_COMPLETED";

    private OnLessonFragmentInteractionListener mListener;

    private String studyId;
    private boolean showCompleted;

    @BindView(R.id.list)
    RecyclerView recyclerView;

    @BindView(R.id.loading_view)
    LoadingView loadingView;

    private SugarLoader<List<Lesson>> mLoader;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LessonsFragment() {
    }

    public static LessonsFragment newInstance(String studyId, boolean showCompleted) {
        LessonsFragment fragment = new LessonsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STUDY_ID, studyId);
        args.putBoolean(ARG_SHOW_COMPLETED, showCompleted);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            studyId = getArguments().getString(ARG_STUDY_ID);
            showCompleted = getArguments().getBoolean(ARG_SHOW_COMPLETED, false);
        }
    }

    private void toggleLoading() {
        if (SQLite.selectCountOf().from(Lesson.class).where(Lesson_Table.studyId.eq(studyId)).count() > 0) {
            loadingView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            loadingView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    OnTableChangedListener tableChangedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_lesson_list, container, false);

        unbinder = ButterKnife.bind(this, view);

        toggleLoading();

        mListener = new OnLessonFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Lesson lesson) {
                onStartLesson(lesson);
            }

            @Override
            public void onMoreButton(Lesson lesson) {

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("moreDialog");
                if (prev != null) {
                    ft.remove(prev);
                }

                ft.addToBackStack(null);

                DialogFragment newFragment = LessonExtrasFragment.newInstance(lesson.id, studyId);
                newFragment.show(ft, "moreDialog");
                getActivity().getSupportFragmentManager().executePendingTransactions();
            }
        };

        mLoader = new SugarLoader<List<Lesson>>("LessonsLoader" + studyId)
                .background(() ->
                        SQLite.select().from(Lesson.class).where(Lesson_Table.studyId.eq(studyId)).and(Lesson_Table.complete.eq(showCompleted)).orderBy(Lesson_Table.index, true).queryList()
                ).onSuccess(lessons -> {
                    LessonRecyclerViewAdapter adapter = (LessonRecyclerViewAdapter)recyclerView.getAdapter();
                    adapter.setLessons(lessons);
                    toggleLoading();
                });

        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new LessonRecyclerViewAdapter(new ArrayList<>(), mListener, context));

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
        if (isDetached() || !isVisible() || !isAdded())
            return;
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

    public void playAudio(Lesson lesson) {
        if (getContext() == null || isDetached()) {
            return;
        }
        Intent intent = new Intent(getContext(), LessonAudioActivity.class);
        intent.putExtra(LessonAudioActivity.ARG_LESSON_ID, lesson.id);
        intent.putExtra(LessonAudioActivity.ARG_START_AUDIO, true);
        getContext().startActivity(intent);
        AppCompatActivity activity = (AppCompatActivity) getContext();
        activity.overridePendingTransition(R.anim.slide_up, R.anim.stay);
    }

    private String expectedLessonID = null;

    public void onStartLesson(Lesson lesson) {

        expectedLessonID = lesson.id;

        File audio = FileHelpers.getAudioFileForLesson(getContext(), lesson);
        if (!audio.exists()) {
            // Download the lesson eh
            LessonResourceManager.getInstance().download(lesson, FileHelpers.FILE_AUDIO, (success, fileUri) -> {
                if (success && expectedLessonID != null && expectedLessonID.equals(lesson.id)) {
                    playAudio(lesson);
                }
            });
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

        void onMoreButton(Lesson lesson);
    }
}
