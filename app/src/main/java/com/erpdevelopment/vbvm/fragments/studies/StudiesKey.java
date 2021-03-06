package com.erpdevelopment.vbvm.fragments.studies;

import android.support.v4.app.Fragment;

import com.google.auto.value.AutoValue;

import com.erpdevelopment.vbvm.application.Key;
import com.erpdevelopment.vbvm.application.MainActivity;

/**
 * Created by thomascarey on 12/07/17.
 */

@AutoValue
public abstract class StudiesKey extends Key {

    public static StudiesKey create() {
        return new AutoValue_StudiesKey();
    }

    @Override
    public String stackIdentifier() {
        return MainActivity.StackType.STUDIES.name();
    }

    @Override
    public String getFragmentTag() {
        return "StudiesKey";
    }

    @Override
    public Fragment createFragment() {
        return StudiesFragment.newInstance();
    }
}
