package com.erpdevelopment.vbvm.fragments.more;

import androidx.fragment.app.Fragment;

import com.erpdevelopment.vbvm.application.Key;
import com.erpdevelopment.vbvm.application.MainActivity;
import com.google.auto.value.AutoValue;

/**
 * Created by thomascarey on 26/09/17.
 */
@AutoValue
abstract public class MoreKey  extends Key {

    public static MoreKey create() {
        return new AutoValue_MoreKey();
    }

    @Override
    public String stackIdentifier() {
        return MainActivity.StackType.MORE.name();
    }

    @Override
    public String getFragmentTag() {
        return "MoreKey";
    }

    @Override
    public Fragment createFragment() {
        return MoreFragment.newInstance();
    }
}
