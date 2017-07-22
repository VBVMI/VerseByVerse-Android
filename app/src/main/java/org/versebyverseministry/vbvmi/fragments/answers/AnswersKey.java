package org.versebyverseministry.vbvmi.fragments.answers;

import android.support.v4.app.Fragment;

import com.google.auto.value.AutoValue;

import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.application.Key;
import org.versebyverseministry.vbvmi.application.MainActivity;

/**
 * Created by thomascarey on 12/07/17.
 */

@AutoValue
public abstract class AnswersKey extends Key {

    public static AnswersKey create() {
        return new AutoValue_AnswersKey();
    }

    @Override
    public String stackIdentifier() {
        return MainActivity.StackType.ANSWERS.name();
    }

    @Override
    public String getFragmentTag() {
        return "AnswersKey";
    }

    @Override
    public Fragment createFragment() {
        return AnswersFragment.newInstance("Answers", "FRAGMENT");
    }
}