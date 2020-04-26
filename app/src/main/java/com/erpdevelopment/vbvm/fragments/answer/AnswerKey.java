package com.erpdevelopment.vbvm.fragments.answer;

import androidx.fragment.app.Fragment;

import com.erpdevelopment.vbvm.application.Key;
import com.erpdevelopment.vbvm.application.MainActivity;
import com.google.auto.value.AutoValue;

/**
 * Created by thomascarey on 2/09/17.
 */

@AutoValue
public abstract class AnswerKey extends Key {
    abstract String answerId();

    public static AnswerKey create(String answerId) {
        return new AutoValue_AnswerKey(answerId);
    }

    @Override
    public String stackIdentifier() {
        return MainActivity.StackType.TOPICS.name();
    }

    @Override
    public String getFragmentTag() {
        return "AnswerKey";
    }

    @Override
    public Fragment createFragment() {
        return AnswerFragment.newInstance(answerId());
    }
}
