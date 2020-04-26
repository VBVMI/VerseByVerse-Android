package com.erpdevelopment.vbvm.fragments.groupStudy;

import androidx.fragment.app.Fragment;

import com.erpdevelopment.vbvm.application.Key;
import com.erpdevelopment.vbvm.application.MainActivity;
import com.google.auto.value.AutoValue;

/**
 * Created by thomascarey on 11/09/17.
 */
@AutoValue
public abstract class GroupStudyKey extends Key {

    abstract String groupStudyId();

//    public static GroupStudyKey create(String groupStudyId) {
//        return new AutoValue_GroupStudyKey(groupStudyId);
//    }

    @Override
    public String stackIdentifier() {
        return MainActivity.StackType.MEDIA.name();
    }

    @Override
    public String getFragmentTag() {
        return "GroupStudyKey";
    }

    @Override
    public Fragment createFragment() {
        return GroupStudyFragment.newInstance(groupStudyId());
    }
}
