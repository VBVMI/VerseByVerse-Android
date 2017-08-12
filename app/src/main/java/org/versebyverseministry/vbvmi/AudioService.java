package org.versebyverseministry.vbvmi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.versebyverseministry.vbvmi.fragments.studies.lesson.LessonAudioActivity;
import org.versebyverseministry.vbvmi.model.Lesson;

import java.util.concurrent.TimeUnit;

public class AudioService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private static final String TAG = "PlayAudio";
    private static final int NOTIFY_ID=1;

    public static String DID_START = "AudioServiceDidStart";

    private MediaPlayer player;

    private Lesson lesson;

    private String lessonFilePath;

    private final IBinder audioBind = new AudioBinder();

    public AudioService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate" + Environment.DIRECTORY_DOWNLOADS);

        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnCompletionListener(this);
        player.setOnPreparedListener(this);
        player.setOnErrorListener(this);
    }

    public void setLesson(Lesson lesson, String filePath) {
        this.lesson = lesson;
        this.lessonFilePath = filePath;
    }

    public Lesson getCurrentLesson() {
        return lesson;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //@IntDef(value = {Service.START_FLAG_REDELIVERY, Service.START_FLAG_RETRY}, flag = true)
        Log.d(TAG, String.format("onStartCommand: flags:%d startId:%d", flags, startId));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return audioBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
//        player.stop();
//        player.release();
        return false;
    }

    public void playAudio() {
        player.reset();

        Uri audioUri = Uri.parse(lessonFilePath);
        try {
            player.setDataSource(getApplicationContext(), audioUri);
        } catch (Exception e) {
            Log.e(TAG, "Error setting data source", e);
        }
        player.prepareAsync();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        Log.d(TAG, "onDestroy:");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

        Intent notificationIntent = new Intent(this, LessonAudioActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_bible_studies_black_24dp)
                .setTicker(lesson.description)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(lesson.description);

        Notification notification = builder.getNotification();

//        startForeground(NOTIFY_ID, notification);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        manager.notify(NOTIFY_ID, notification);

        Intent intent = new Intent(DID_START);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public int getPosition() {
        return player.getCurrentPosition();
    }

    public int getDuration() {
        return player.getDuration();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
    }

    public void seekTo(int positionMsec) {
        player.seekTo(positionMsec);
    }

    public void start() {
        player.start();
    }

    public void jumpForward() {
        int position = getPosition();
        int duration = getDuration();
        int jump = (int) TimeUnit.SECONDS.toMillis(30);
        int destination = Math.min(position + jump, duration);
        seekTo(destination);
    }

    public void jumpBack() {
        int position = getPosition();
        int jump = (int) TimeUnit.SECONDS.toMillis(30);
        int destination = Math.max(position - jump, 0);
        seekTo(destination);
    }

    public class AudioBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }
}
