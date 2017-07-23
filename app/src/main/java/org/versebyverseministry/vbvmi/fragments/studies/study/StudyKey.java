package org.versebyverseministry.vbvmi.fragments.studies.study;

import android.support.v4.app.Fragment;

import com.google.auto.value.AutoValue;

import org.versebyverseministry.vbvmi.application.Key;
import org.versebyverseministry.vbvmi.application.MainActivity;
import org.versebyverseministry.vbvmi.fragments.studies.study.AutoValue_StudyKey;

/**
 * Created by thomascarey on 16/07/17.
 */

@AutoValue
public abstract class StudyKey extends Key {

    abstract String studyId();

    public static StudyKey create(String studyId) {
        return new AutoValue_StudyKey(studyId);
    }

    @Override
    public String stackIdentifier() {
        return MainActivity.StackType.ANSWERS.name();
    }

    @Override
    public String getFragmentTag() {
        return "StudyKey";
    }

    @Override
    public Fragment createFragment() {
        return StudyFragment.newInstance(studyId());
    }
}
