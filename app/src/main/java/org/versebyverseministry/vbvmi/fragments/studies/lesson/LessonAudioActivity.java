package org.versebyverseministry.vbvmi.fragments.studies.lesson;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.versebyverseministry.vbvmi.AudioController;
import org.versebyverseministry.vbvmi.AudioService;
import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.model.Lesson;
import org.versebyverseministry.vbvmi.model.Lesson_Table;
import org.versebyverseministry.vbvmi.model.Study;
import org.versebyverseministry.vbvmi.model.Study_Table;
import org.w3c.dom.Text;

import java.util.Formatter;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LessonAudioActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {

    private static String TAG = "LessonAudioActivity";

    public static String ARG_LESSON_ID = "ARG_LESSON_ID";
    public static String ARG_LESSON_PATH = "ARG_LESSON_PATH";

    private Lesson lesson;
    private Study study;
    private String lessonAudioPath;
    private boolean mDragging;

    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private AudioService audioService;
    private Intent playIntent;
    private boolean audioBound = false;

    private WindowManager mWindowManager;


    private AudioController controller;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.time_current)
    TextView timeCurrentTextView;

    @BindView(R.id.timeRemaining)
    TextView timeRemainingTextView;

    @BindView(R.id.mediacontroller_progress)
    SeekBar seekBar;

    @BindView(R.id.titleView)
    TextView titleView;

    @BindView(R.id.descriptionView)
    TextView descriptionView;

    @BindView(R.id.rew)
    ImageButton jumpBackButton;

    @BindView(R.id.pause)
    ImageButton playPauseButton;

    @BindView(R.id.ffwd)
    ImageButton jumpForwardButton;

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
        mWindowManager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int imageWidth = displayMetrics.widthPixels;
        String studyImageURL = study.imageForWidth(imageWidth);
        if (studyImageURL != null) {
            Glide.with(this).load(studyImageURL).into(imageView);
        } else {
            Glide.with(this).load(study.thumbnailSource).into(imageView);
        }

        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        getWindow().setVolumeControlStream(AudioManager.STREAM_MUSIC);
//        playButton.setOnClickListener(v -> {
//            audioService.setLesson(lesson, lessonAudioPath);
//            audioService.playAudio();
//        });

        //setController();
        titleView.setText(lesson.title);

        descriptionView.setText(lesson.description);

        jumpForwardButton.setOnClickListener(v -> {
            jumpForward();
        });

        jumpBackButton.setOnClickListener(v -> {
            jumpBack();
        });

        playPauseButton.setOnClickListener(v -> {
            if (isPlaying()) {
                pause();
            } else {
                start();
            }
            updatePausePlay();
        });


        seekBar.setOnSeekBarChangeListener(mSeekListener);
        seekBar.setMax(100);
    }



    private ServiceConnection audioConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.AudioBinder binder = (AudioService.AudioBinder)service;

            audioService = binder.getService();

            audioService.setLesson(lesson, lessonAudioPath);

            audioBound = true;

            audioService.playAudio();
            updatePausePlay();
            //controller.show();
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

        playPauseButton.post(mShowProgress);
    }

    @Override
    protected void onPause() {
        playPauseButton.removeCallbacks(mShowProgress);
        super.onPause();

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

    private void updatePausePlay() {
        if (playPauseButton == null)
            return;

        if (isPlaying()) {
            playPauseButton.setImageResource(R.drawable.ic_pause_button_40dp);
            playPauseButton.setContentDescription("Pause");
        } else {
            playPauseButton.setImageResource(R.drawable.ic_play_button_40dp);
            playPauseButton.setContentDescription("Play");
        }

    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    private final Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (!mDragging && isPlaying()) {
                playPauseButton.postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    private int setProgress() {
        if (audioBound == false || mDragging) {
            return 0;
        }
        int position = getCurrentPosition();
        int duration = getDuration();
        if (seekBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 100L * position / duration;
                seekBar.setProgress( (int) pos);
            }
            int percent = getBufferPercentage();
            seekBar.setSecondaryProgress(percent * 10);
        }

        if (timeRemainingTextView != null)
            timeRemainingTextView.setText(stringForTime(duration));
        if (timeCurrentTextView != null)
            timeCurrentTextView.setText(stringForTime(position));

//        updatePausePlay();
        return position;
    }

//    private void setController() {
//        controller = new AudioController(this);
//        controller.setPrevNextListeners(v -> {
//            jumpForward();
//        }, v -> {
//            jumpBack();
//        });
//
//        controller.setMediaPlayer(this);
//        controller.setAnchorView(playerLayout);
//    }

    private void jumpBack() {
        audioService.jumpBack();
    }

    private void jumpForward() {
        audioService.jumpForward();
    }

    @Override
    public void start() {
        audioService.start();
        playPauseButton.post(mShowProgress);
    }

    @Override
    public void pause() {
        audioService.pausePlayer();
    }

    @Override
    public int getDuration() {
        return audioService.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return audioService.getPosition();
    }

    @Override
    public void seekTo(int pos) {
        audioService.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return audioService.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }


    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {

            mDragging = true;

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            playPauseButton.removeCallbacks(mShowProgress);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = getDuration();
            long newposition = (duration * progress) / 100L;
            seekTo( (int) newposition);
            if (timeCurrentTextView != null)
                timeCurrentTextView.setText(stringForTime( (int) newposition));
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
            updatePausePlay();

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            playPauseButton.post(mShowProgress);
        }
    };
}
