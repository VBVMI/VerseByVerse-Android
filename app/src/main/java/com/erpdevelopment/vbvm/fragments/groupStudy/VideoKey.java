package com.erpdevelopment.vbvm.fragments.groupStudy;

import androidx.fragment.app.Fragment;

import com.erpdevelopment.vbvm.application.Key;
import com.erpdevelopment.vbvm.application.MainActivity;
import com.google.auto.value.AutoValue;

/**
 * Created by thomascarey on 23/09/17.
 */

@AutoValue
abstract public class VideoKey extends Key {

    abstract String channelId();

//    public static VideoKey create(String channelId) {
//        return new AutoValue_VideoKey(channelId);
//    }

    @Override
    public String stackIdentifier() {
        return MainActivity.StackType.MEDIA.name();
    }

    @Override
    public String getFragmentTag() {
        return "VideoKey";
    }

    @Override
    public Fragment createFragment() {
        return VideosFragment.newInstance(channelId());
    }

}
