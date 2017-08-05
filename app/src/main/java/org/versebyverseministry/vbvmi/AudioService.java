package org.versebyverseministry.vbvmi;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.IntDef;
import android.util.Log;

import org.versebyverseministry.vbvmi.model.Lesson;

public class AudioService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private static final String TAG = "PlayAudio";

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
        player.stop();
        player.release();
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
        super.onDestroy();
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
    }

    public class AudioBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }
}
