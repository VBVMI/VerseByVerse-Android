package org.versebyverseministry.vbvmi.fragments.studies;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.fragments.shared.AbstractFragment;
import org.versebyverseministry.vbvmi.model.Lesson;
import org.versebyverseministry.vbvmi.model.Lesson_Table;
import org.versebyverseministry.vbvmi.model.Study;
import org.versebyverseministry.vbvmi.model.Study_Table;
import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudyFragment extends AbstractFragment {
    private static final String ARG_STUDY_ID = "ARG_STUDY_ID";

    private Study study;

    private List<Lesson> lessons;

    @BindView(R.id.textView)
    TextView textView;

    @BindView(R.id.textView2)
    TextView textView2;

    private FlowContentObserver observer;

    private FlowContentObserver.OnModelStateChangedListener modelStateChangedListener;

    public StudyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param studyId The id of the Study.
     * @return A new instance of fragment StudyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StudyFragment newInstance(String studyId) {
        StudyFragment fragment = new StudyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STUDY_ID, studyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_study, container, false);
        unbinder = ButterKnife.bind(this, v);

        final Handler mainHandler = new Handler(getContext().getMainLooper());

        modelStateChangedListener = new FlowContentObserver.OnModelStateChangedListener() {
            @Override
            public void onModelStateChanged(@Nullable Class<?> table, BaseModel.Action action, @NonNull SQLOperator[] primaryKeyValues) {
                Log.d("LESSON", "state changed");

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        configureView();
                    }
                });
            }
        };

        observer = new FlowContentObserver();
        observer.addModelChangeListener(modelStateChangedListener);
        observer.registerForContentChanges(getContext(), Lesson.class);

        configureView();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("LESSON", "onPause: Lessons");
    }

    @Override
    public void onDestroy() {
        Log.d("LESSON", "onDestroy: Lessons");
        observer.removeModelChangeListener(modelStateChangedListener);
        observer.unregisterForContentChanges(getContext());
        super.onDestroy();
    }

    private void configureView() {
        String studyId = getArguments().getString(ARG_STUDY_ID);
        study = SQLite.select().from(Study.class).where(Study_Table.id.eq(studyId)).querySingle();
        lessons = SQLite.select().from(Lesson.class).where(Lesson_Table.studyId.eq(studyId)).queryList();

        textView.setText(study.title);

        textView2.setText("There are " + lessons.size() + " lessons downloaded");
    }

}
