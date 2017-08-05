package org.versebyverseministry.vbvmi.fragments.studies.lesson;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.versebyverseministry.vbvmi.AudioService;
import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.model.Lesson;
import org.versebyverseministry.vbvmi.model.Lesson_Table;
import org.versebyverseministry.vbvmi.model.Study;
import org.versebyverseministry.vbvmi.model.Study_Table;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LessonAudioActivity extends AppCompatActivity {

    private static String TAG = "LessonAudioActivity";

    public static String ARG_LESSON_ID = "ARG_LESSON_ID";
    public static String ARG_LESSON_PATH = "ARG_LESSON_PATH";

    private Lesson lesson;
    private Study study;
    private String lessonAudioPath;


    private AudioService audioService;
    private Intent playIntent;
    private boolean audioBound = false;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.playButton)
    Button playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_audio);

        String lessonId = getIntent().getExtras().getString(ARG_LESSON_ID);
        lessonAudioPath = getIntent().getExtras().getString(ARG_LESSON_PATH);
        lesson = SQLite.select().from(Lesson.class).where(Lesson_Table.id.eq(lessonId)).querySingle();
        study = SQLite.select().from(Study.class).where(Study_Table.id.eq(lesson.studyId)).querySingle();

        ButterKnife.bind(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

        int imageWidth = displayMetrics.widthPixels;
        String studyImageURL = study.imageForWidth(imageWidth);
        if (studyImageURL != null) {
            Glide.with(this).load(studyImageURL).into(imageView);
        } else {
            Glide.with(this).load(study.thumbnailSource).into(imageView);
        }

        playButton.setOnClickListener(v -> {
            audioService.setLesson(lesson, lessonAudioPath);
            audioService.playAudio();
        });
    }

    private ServiceConnection audioConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.AudioBinder binder = (AudioService.AudioBinder)service;

            audioService = binder.getService();

            audioService.setLesson(lesson, lessonAudioPath);

            audioBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            audioBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null) {
            playIntent = new Intent(this, AudioService.class);
            bindService(playIntent, audioConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        audioService = null;

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
}
