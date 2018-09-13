package com.erpdevelopment.vbvm;

import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

public class RemoteController extends MediaSessionCompat.Callback {

    static String TAG = "RemoteController";


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: YO");
    }

    @Override
    public void onPlay() {
        super.onPlay();
        Log.d(TAG, "onPlay: YO");
    }
}
