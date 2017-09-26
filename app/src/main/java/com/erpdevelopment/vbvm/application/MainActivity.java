package com.erpdevelopment.vbvm.application;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.crashlytics.android.Crashlytics;
import com.erpdevelopment.vbvm.BottomNavigationViewHelper;
import com.erpdevelopment.vbvm.api.DatabaseManager;
import com.erpdevelopment.vbvm.fragments.media.MediaKey;
import com.erpdevelopment.vbvm.fragments.more.MoreKey;
import com.erpdevelopment.vbvm.fragments.topics.TopicsKey;
import com.erpdevelopment.vbvm.fragments.studies.StudiesKey;
import com.erpdevelopment.vbvm.model.Answer;
import com.erpdevelopment.vbvm.model.Article;
import com.erpdevelopment.vbvm.model.Channel;
import com.erpdevelopment.vbvm.model.GroupStudy;
import com.erpdevelopment.vbvm.util.Multistack;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

import com.erpdevelopment.vbvm.AudioService;
import com.erpdevelopment.vbvm.R;
import com.erpdevelopment.vbvm.fragments.studies.lesson.LessonAudioActivity;
import com.erpdevelopment.vbvm.model.Lesson;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import static com.erpdevelopment.vbvm.application.MainActivity.StackType.MEDIA;
import static com.erpdevelopment.vbvm.application.MainActivity.StackType.MORE;
import static com.erpdevelopment.vbvm.application.MainActivity.StackType.STUDIES;
import static com.erpdevelopment.vbvm.application.MainActivity.StackType.TOPICS;

public class MainActivity extends AppCompatActivity implements StateChanger {

    private static final String TAG = "MainActivity";

    public enum StackType {
        STUDIES,
        TOPICS,
        MEDIA,
        MORE
    }

    private AudioService audioService;
    private boolean audioBound = false;
    private Intent playIntent;

    @BindView(R.id.root)
    RelativeLayout root;

    @BindView(R.id.coordinator_root)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;


    @BindView(R.id.currently_playing_layout_background)
    RelativeLayout audioBarLayoutBackground;

    @BindView(R.id.currently_playing_layout)
    RelativeLayout audioBarLayout;

    @BindView(R.id.audio_title)
    TextView audioTitle;

    @BindView(R.id.audio_play_pause_button)
    ImageButton playPauseButton;

    @BindView(R.id.audio_progress_bar)
    ProgressBar audioProgressBar;

    Multistack multistack;

    private boolean isAnimating; // unfortunately, we must manually ensure that you can't navigate while you're animating.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.multistack = new Multistack();

        multistack.add(STUDIES.name(), new BackstackDelegate(null));
        multistack.add(TOPICS.name(), new BackstackDelegate(null));
        multistack.add(MEDIA.name(), new BackstackDelegate(null));
        multistack.add(MORE.name(), new BackstackDelegate(null));

        Multistack.NonConfigurationInstance nonConfigurationInstance = (Multistack.NonConfigurationInstance) getLastCustomNonConfigurationInstance();

        multistack.onCreate(savedInstanceState);

        multistack.onCreate(STUDIES.name(), savedInstanceState, nonConfigurationInstance, StudiesKey.create());
        multistack.onCreate(TOPICS.name(), savedInstanceState, nonConfigurationInstance, TopicsKey.create());
        multistack.onCreate(MEDIA.name(), savedInstanceState, nonConfigurationInstance, MediaKey.create());
        multistack.onCreate(MORE.name(), savedInstanceState, nonConfigurationInstance, MoreKey.create());

        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        BottomNavigationViewHelper.disableShiftMode(bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String newStack = null;
                switch (item.getItemId()) {
                    case R.id.navigation_studies:
                        newStack = STUDIES.name();
                        break;
                    case R.id.navigation_answers:
                        newStack = TOPICS.name();
                        break;
                    case R.id.navigation_videos:
                        newStack = MEDIA.name();
                        break;
                    case R.id.navigation_more:
                        newStack = MORE.name();
                        break;
                }

                if (multistack.getSelectedStackIdentifier() != newStack) {
                    removePreviousFragment();
                    multistack.setSelectedStack(newStack);
                }
                return true;
            }
        });

        multistack.setStateChanger(this);


        DatabaseManager.observer.registerForContentChanges(this, Lesson.class);
        DatabaseManager.observer.registerForContentChanges(this, Article.class);
        DatabaseManager.observer.registerForContentChanges(this, Answer.class);
        DatabaseManager.observer.registerForContentChanges(this, Channel.class);
        DatabaseManager.observer.registerForContentChanges(this, GroupStudy.class);
//        DatabaseManager.observer.registerForContentChanges(this, Category.class);
//        DatabaseManager.observer.registerForContentChanges(this, Study.class);

        audioBarLayout.setOnClickListener(v -> {
            Log.d(TAG, "Tapped bar");
            Lesson lesson = audioService.getCurrentLesson();
            Intent intent = new Intent(this, LessonAudioActivity.class);
            intent.putExtra(LessonAudioActivity.ARG_LESSON_ID, lesson.id);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up, R.anim.stay);
        });

        playPauseButton.setOnClickListener(v -> {
            if (audioBound) {
                if (isPlaying()) {
                    audioService.pausePlayer();
                } else {
                    audioService.start();
                    playPauseButton.post(mShowProgress);
                }
                updatePausePlay();
            }
        });
    }


    public Multistack getMultistack() {
        return multistack;
    }

    private void removePreviousFragment() {
        if (previousFragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().disallowAddToBackStack();
            fragmentTransaction.remove(previousFragment);
            fragmentTransaction.commit();
        }
    }

    public static MainActivity get(Context context) {
        // noinspection ResourceType
        return (MainActivity) context.getSystemService(TAG);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return multistack.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        multistack.onPostResume();
    }

    @Override
    public void onBackPressed() {
        if (!multistack.onBackPressed()) {
            super.onBackPressed();
        }
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

    private BroadcastReceiver audioDidStartReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updatePausePlay();
            playPauseButton.post(mShowProgress);
        }
    };

    private ServiceConnection audioConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.AudioBinder binder = (AudioService.AudioBinder)service;

            audioService = binder.getService();

            audioBound = true;

            Lesson lesson = audioService.getCurrentLesson();
            if (lesson != null) {
                audioTitle.setText(lesson.title);
            }

            audioBarLayoutBackground.setVisibility(View.VISIBLE);

            updatePausePlay();
            playPauseButton.post(mShowProgress);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            audioBound = false;
            audioBarLayoutBackground.setVisibility(View.GONE);
        }
    };

    private final Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (isPlaying()) {
                playPauseButton.postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };

    private boolean isPlaying() {
        if (audioService == null) {
            return false;
        }
        return audioService.isPlaying();
    }

    private int getCurrentPosition() {
        if (audioService == null) {
            return 0;
        }
        return audioService.getPosition();
    }

    int getDuration() {
        return audioService.getDuration();
    }

    private int setProgress() {
        if (audioBound == false) {
            return 0;
        }
        int position = getCurrentPosition();
        int duration = getDuration();
        if (audioProgressBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 100L * position / duration;
                audioProgressBar.setProgress( (int) pos);
            }
        }

        return position;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(audioDidStartReciever, new IntentFilter(AudioService.DID_START));
        if(playIntent==null) {
            playIntent = new Intent(this, AudioService.class);
        }
        if (isAudioServiceRunning()) {
            bindService(playIntent, audioConnection, Context.BIND_AUTO_CREATE);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        multistack.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(audioDidStartReciever);
        Log.d(TAG, "onPause");
        if (audioBound) {
            unbindService(audioConnection);
            playIntent = null;
        }
        super.onPause();
    }

    private boolean isAudioServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if("com.erpdevelopment.vbvm.AudioService".equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        multistack.persistViewToState(root.getChildAt(0));
        multistack.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        DatabaseManager.observer.unregisterForContentChanges(this);
        multistack.onDestroy();
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public Object getSystemService(@NonNull  String name) {
        if(multistack != null) {
            BackstackDelegate stack = multistack.get(name);
            if(stack != null) {
                return stack;
            }
        }
        if(TAG.equals(name)) {
            return this;
        }
        return super.getSystemService(name);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return !isAnimating && super.dispatchTouchEvent(ev); // unfortunately, we must manually make sure you can't navigate while you're animating.
    }

    Fragment previousFragment;

    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            // no-op
            completionCallback.stateChangeComplete();
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().disallowAddToBackStack();

        if(stateChange.getDirection() == StateChange.FORWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        } else if(stateChange.getDirection() == StateChange.BACKWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }

        for(Object _oldKey : stateChange.getPreviousState()) {
            Key oldKey = (Key) _oldKey;
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(oldKey.getFragmentTag());
            if(fragment != null) {
                if(!stateChange.getNewState().contains(oldKey)) {
                    Log.i(TAG, "Old key is NOT in new state: removing [" + oldKey + "]");
                    fragmentTransaction.remove(fragment);
                } else if(!fragment.isDetached()) {
                    Log.i(TAG, "Old key is in new state, but not showing: detaching [" + oldKey + "]");
                    fragmentTransaction.detach(fragment);
                }
            }
        }

        for(Object _newKey : stateChange.getNewState()) {
            Key newKey = (Key) _newKey;
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(newKey.getFragmentTag());
            previousFragment = fragment;
            if(newKey.equals(stateChange.topNewState())) {
                if(fragment != null) {
                    if(fragment.isDetached()) {
                        Log.i(TAG, "New key is top state but detached: reattaching [" + newKey + "]");
                        fragmentTransaction.attach(fragment);
                    } else {
                        Log.i(TAG, "New key is top state but already attached: probably config change for [" + newKey + "]");
                    }
                } else {
                    Log.i(TAG, "New fragment does not exist yet, adding [" + newKey + "]");
                    fragment = newKey.createFragment();
                    previousFragment = fragment;
                    fragmentTransaction.add(R.id.root, fragment, newKey.getFragmentTag());
                }
            } else {
                if(fragment != null && !fragment.isDetached()) {
                    Log.i(TAG, "New fragment is not active fragment. It should be detached: [" + newKey + "]");
                    fragmentTransaction.detach(fragment);
                } else {
                    Log.i(TAG, "New fragment is already detached or doesn't exist, as expected: [" + newKey + "]");
                }
            }
        }
        fragmentTransaction.commitNow();
        completionCallback.stateChangeComplete();
    }
}
