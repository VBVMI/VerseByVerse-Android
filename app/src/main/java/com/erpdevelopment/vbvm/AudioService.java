package com.erpdevelopment.vbvm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.erpdevelopment.vbvm.fragments.studies.lesson.LessonAudioActivity;
import com.erpdevelopment.vbvm.model.Lesson;
import com.erpdevelopment.vbvm.model.MetaData;
import com.erpdevelopment.vbvm.model.Study;
import com.erpdevelopment.vbvm.model.Study_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.fabric.sdk.android.Fabric;

public class AudioService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private static final String TAG = "PlayAudio";
    private static final int NOTIFY_ID=142345;

    public static String DID_START = "AudioServiceDidStart";
    public static String DID_END = "AudioServiceDidEnd";

    private MediaPlayer mMediaPlayer;

    private Lesson lesson;
    private Study study;
    private MediaSessionCompat mMediaSessionCompat;
    private PlaybackStateCompat playbackState;
    private String lessonFilePath;

    private final IBinder audioBind = new AudioBinder();

    private Handler periodicHandler = new Handler();

    private MediaMetadataCompat metadataCompat;

    private Bitmap theBitmap = null;

    private MediaSessionCompat.Callback mMediaSessionCallback = new MediaSessionCompat.Callback() {

        @Override
        public void onPlay() {
            super.onPlay();

            play();
        }

        @Override
        public void onPause() {
            super.onPause();

            pausePlayer();
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
        }

    };

    private boolean noisyReceiverRegistered = false;

    public AudioService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate" + Environment.DIRECTORY_DOWNLOADS);

        initMediaPlayer();
        initMediaSession();
    }

    private void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
//        mMediaPlayer.setVolume(1.0f, 1.0f);
    }

    public void initMediaSession() {


        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "🔥 Audio", mediaButtonReceiver, null);

        mMediaSessionCompat.setCallback(mMediaSessionCallback);
        mMediaSessionCompat.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        );

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
        mMediaSessionCompat.setMediaButtonReceiver(pendingIntent);

    }

    public void setLesson(Lesson lesson) {

        this.lesson = lesson;
        this.lessonFilePath = FileHelpers.audioFilePathForLesson(this, lesson).getPath();
        study = SQLite.select().from(Study.class).where(Study_Table.id.eq(lesson.studyId)).querySingle();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    theBitmap = Glide.
                            with(getApplicationContext()).
                            load(study.image900).
                            asBitmap().
                            into(-1,-1).
                            get();
                } catch (final ExecutionException e) {
                    Log.e(TAG, e.getMessage());
                } catch (final InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void dummy) {
                if (null != theBitmap) {
                    // The full bitmap should be available here
                    initMediaSessionMetadata();
                    Log.d(TAG, "Image loaded");
                };
            }
        }.execute();
    }

    private void initMediaSessionMetadata() {
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
        //Notification icon in card
        if (theBitmap != null) {
            metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, theBitmap);
            metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, theBitmap);
            metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, theBitmap);
        } else {
//            metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//            metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        }

        //lock screen icon for pre lollipop

        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, lesson.title);
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, study.title);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, 1);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, 1);

        mMediaSessionCompat.setMetadata(metadataBuilder.build());
    }

    public Lesson getCurrentLesson() {
        return lesson;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //@IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true)
        Log.d(TAG, String.format("onStartCommand: flags:%d startId:%d", flags, startId));
        MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return audioBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
//        mMediaPlayer.stop();
//        mMediaPlayer.release();
        return false;
    }

    private void testShutdown() {
        if (!isPlaying()) {
            AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            mAudioManager.abandonAudioFocus(afChangeListener);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(NOTIFY_ID);
            //stopSelf();
            if (mMediaSessionCompat != null) {
                Log.d(TAG, "Releasing Media Session");
                mMediaSessionCompat.release(); // DONT NOT CALL THIS
            }

            if (mNoisyReceiver != null && noisyReceiverRegistered) {
                unregisterReceiver(mNoisyReceiver);
                noisyReceiverRegistered = false;
            }
        }
    }

    private boolean playOnPrepare = false;

    private boolean dontCompleteLesson = false;

    public void prepare() {
        dontCompleteLesson = true;
        mMediaPlayer.reset();
        playOnPrepare = false;
        Uri audioUri = Uri.parse(lessonFilePath);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), audioUri);
        } catch (Exception e) {
            Log.e(TAG, "Error setting data source", e);
        }
        mMediaPlayer.prepareAsync();
    }

    public void playAudio() {
        dontCompleteLesson = true;
        mMediaPlayer.reset();
        playOnPrepare = true;

        Uri audioUri = Uri.parse(lessonFilePath);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), audioUri);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            Fabric.getLogger().e(TAG, "Error setting data source", e);
            Crashlytics.logException(e);
        }

        configureState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(afChangeListener);
        if (noisyReceiverRegistered) {
            unregisterReceiver(mNoisyReceiver);
            noisyReceiverRegistered = false;
        }
        mMediaSessionCompat.release();
        NotificationManagerCompat.from(this).cancel(NOTIFY_ID);

        stopForeground(true);
        Log.d(TAG, "onDestroy:");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAudioManager.abandonAudioFocus(afChangeListener);



        if (!dontCompleteLesson && lesson != null) {
            lesson.progress = 0;
            lesson.complete = true;
            lesson.save();

            MetaData metaData = SQLite.select().from(MetaData.class).querySingle();
            if (metaData != null) {
                metaData.currentLessonId = null;
                metaData.save();
            }
        }
        dontCompleteLesson = false;

        stopRepeatingTask();

        Log.d(TAG, "Audio service did end");
        Intent intent = new Intent(DID_END);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        stopHandle.postDelayed(stopRunnable, 1000 * 20);
//        mMediaSessionCompat.release(); // DONT NOT CALL THIS
    }

    private BroadcastReceiver mNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Pause the music
            mMediaPlayer.pause();
        }
    };

    /*

    // On Play



     */

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    private AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.d(TAG, "audio focus change: " + focusChange);
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // Bail we aren't getting focus back
                pausePlayer();
                testShutdown();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                pausePlayer();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                pausePlayer();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                play();
            }
        }
    };

    @Override
    public void onPrepared(MediaPlayer mp) {
        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (lesson != null) {
            lesson.complete = false;
            lesson.save();
        }

        MetaData metaData = SQLite.select().from(MetaData.class).querySingle();
        if (metaData != null) {
            metaData.currentLessonId = lesson.id;
            metaData.save();
        }

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d(TAG, "Focus granted");

            mMediaSessionCompat.setActive(true);


            initMediaSessionMetadata();

        }

        if (lesson != null) {
            double progress = lesson.progress;
            if(progress != 0 && progress != 1) {
                seekTo((int)(progress * (double)getDuration()));
            }
        }

        if (playOnPrepare) {
            mp.start();

            startRepeatingTask();

            showPlayingNotification();

            Intent intent = new Intent(DID_START);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            if (mNoisyReceiver != null && !noisyReceiverRegistered) {
                IntentFilter filter = new IntentFilter((AudioManager.ACTION_AUDIO_BECOMING_NOISY));
                registerReceiver(mNoisyReceiver, filter);
                noisyReceiverRegistered = true;
            }

            configureState();

        }
    }

    private void configureState() {
        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();
        builder.setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_REWIND | PlaybackStateCompat.ACTION_FAST_FORWARD);
        if (mMediaPlayer.isPlaying()) {
            builder.setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1);
        } else {
            builder.setState(PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1);
        }

        playbackState = builder.build();
        mMediaSessionCompat.setPlaybackState(playbackState);
    }

    private void showPlayingNotification() {
        NotificationCompat.Builder builder = MediaStyleHelper.from(AudioService.this, mMediaSessionCompat);
        if( builder == null ) {
            return;
        }

        Intent notificationIntent = new Intent(this, LessonAudioActivity.class);
        notificationIntent.putExtra(LessonAudioActivity.ARG_LESSON_ID, lesson.id);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        builder.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0)
                .setMediaSession(mMediaSessionCompat.getSessionToken())
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(), PlaybackStateCompat.ACTION_STOP)));
        builder.setContentIntent(pendingIntent);

        builder.setSmallIcon(R.drawable.ic_book_notification);
        NotificationManagerCompat.from(AudioService.this).notify(NOTIFY_ID, builder.build());
    }

    private void showPausedNotification() {
        NotificationCompat.Builder builder = MediaStyleHelper.from(this, mMediaSessionCompat);
        if( builder == null ) {
            return;
        }

        Intent notificationIntent = new Intent(this, LessonAudioActivity.class);
        notificationIntent.putExtra(LessonAudioActivity.ARG_LESSON_ID, lesson.id);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        builder.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0)
                .setMediaSession(mMediaSessionCompat.getSessionToken())
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getApplicationContext(), PlaybackStateCompat.ACTION_STOP)));
        builder.setContentIntent(pendingIntent);



        builder.setSmallIcon(R.drawable.ic_book_notification);
        NotificationManagerCompat.from(this).notify(NOTIFY_ID, builder.build());
    }

    private void setMediaPlaybackState(int state) {
        PlaybackStateCompat.Builder playbackstateBuilder = new PlaybackStateCompat.Builder();
        if( state == PlaybackStateCompat.STATE_PLAYING ) {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PAUSE);
        } else {
            playbackstateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY);
        }
        playbackstateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        mMediaSessionCompat.setPlaybackState(playbackstateBuilder.build());
    }

    private double getProgress() {
        double position = getPosition();
        double duration = getDuration();

        if (duration == 0) {
            return 0;
        }

        return position / duration;
    }

    public int getPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    Handler stopHandle = new Handler();

    Runnable stopRunnable = new Runnable() {
        @Override
        public void run() {
            testShutdown();
        }
    };

    public void pausePlayer() {
        if (!mMediaPlayer.isPlaying()) {
            return;
        }

        mMediaPlayer.pause();

        setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
        showPausedNotification();

        stopRepeatingTask();
        updateProgress();

        stopHandle.removeCallbacks(stopRunnable);
        stopHandle.postDelayed(stopRunnable, 1000 * 20);

        if (mNoisyReceiver != null && noisyReceiverRegistered){
            unregisterReceiver(mNoisyReceiver);
            noisyReceiverRegistered = false;
        }

        configureState();
    }

    public void seekTo(int positionMsec) {
        mMediaPlayer.seekTo(positionMsec);
    }

    public void start() {
        stopHandle.removeCallbacks(stopRunnable);
        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            play();
        }
    }

    private void play() {
        if( !successfullyRetrievedAudioFocus() ) {
            return;
        }

        mMediaSessionCompat.setActive(true);
        setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);

        showPlayingNotification();

        mMediaPlayer.start();

        startRepeatingTask();
    }

    public void jumpForward() {
        int position = getPosition();
        int duration = getDuration();
        int jump = (int) TimeUnit.SECONDS.toMillis(30);
        int destination = Math.min(position + jump, duration);
        seekTo(destination);
        updateProgress();
    }

    public void jumpBack() {
        int position = getPosition();
        int jump = (int) TimeUnit.SECONDS.toMillis(30);
        int destination = Math.max(position - jump, 0);
        seekTo(destination);
        updateProgress();
    }

    public class AudioBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    private void updateProgress() {
        double progress = getProgress();
        if (lesson != null) {
            lesson.progress = progress;
            lesson.save();
        }
    }

    Runnable progressChecker = new Runnable() {
        @Override
        public void run() {
            try {
                updateProgress();
            } finally {
                periodicHandler.postDelayed(progressChecker, 5000);
            }
        }
    };

    private boolean isRunningRepeatingTask = false;

    private synchronized void startRepeatingTask() {
        if (!isRunningRepeatingTask) {
            progressChecker.run();
        }
        isRunningRepeatingTask = true;
    }

    private synchronized void stopRepeatingTask() {
        if (isRunningRepeatingTask) {
            periodicHandler.removeCallbacks(progressChecker);
        }
        isRunningRepeatingTask = false;
    }

    private boolean successfullyRetrievedAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int result = audioManager.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        return result == AudioManager.AUDIOFOCUS_GAIN;
    }

}
