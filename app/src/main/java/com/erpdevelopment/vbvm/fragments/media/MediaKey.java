package com.erpdevelopment.vbvm.fragments.media;

import androidx.fragment.app.Fragment;

import com.erpdevelopment.vbvm.application.Key;
import com.erpdevelopment.vbvm.application.MainActivity;
import com.google.auto.value.AutoValue;

/**
 * Created by thomascarey on 9/09/17.
 */

@AutoValue
public abstract class MediaKey extends Key {

    public static MediaKey create() {
        return new AutoValue_MediaKey();
    }

    @Override
    public String stackIdentifier() {
        return MainActivity.StackType.MEDIA.name();
    }

    @Override
    public String getFragmentTag() {
        return "MediaKey";
    }

    @Override
    public Fragment createFragment() {
        return MediaFragment.newInstance();
    }
}