package org.versebyverseministry.vbvmi.fragments.studies.study;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.api.DatabaseManager;
import org.versebyverseministry.vbvmi.fragments.shared.AbstractFragment;
import org.versebyverseministry.vbvmi.fragments.studies.lesson.LessonAudioActivity;
import org.versebyverseministry.vbvmi.model.Lesson;
import org.versebyverseministry.vbvmi.model.Lesson_Table;

import java.util.List;

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

        if (getArguments() != null) {
            studyId = getArguments().getString(ARG_STUDY_ID);
        }
    }

    OnTableChangedListener tableChangedListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lesson_list, container, false);


        List<Lesson> lessons = SQLite.select().from(Lesson.class).where(Lesson_Table.studyId.eq(studyId)).orderBy(Lesson_Table.index, true).queryList();


        mListener = new OnLessonFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(Lesson lesson) {
                Intent intent = new Intent(getContext(), LessonAudioActivity.class);
                intent.putExtra(LessonAudioActivity.ARG_LESSON_ID, lesson.id);
                getContext().startActivity(intent);
                AppCompatActivity activity = (AppCompatActivity) getContext();
                activity.overridePendingTransition(R.anim.slide_up, R.anim.stay);
            }
        };

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            this.view = recyclerView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyLessonRecyclerViewAdapter(lessons, mListener));
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

    public void reloadData() {
        MyLessonRecyclerViewAdapter adapter = (MyLessonRecyclerViewAdapter)view.getAdapter();
        List<Lesson> lessons = SQLite.select().from(Lesson.class).where(Lesson_Table.studyId.eq(studyId)).orderBy(Lesson_Table.index, true).queryList();
        adapter.setLessons(lessons);
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
