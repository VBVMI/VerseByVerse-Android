package com.erpdevelopment.vbvm.fragments.answers;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.auto.value.AutoValue;

import com.erpdevelopment.vbvm.application.Key;
import com.erpdevelopment.vbvm.application.MainActivity;

/**
 * Created by thomascarey on 12/07/17.
 */

@AutoValue
public abstract class TopicsKey extends Key {

    @Nullable
    abstract String topicId();

    public static TopicsKey create() {
        return createWithTopic(null);
    }

    public static TopicsKey createWithTopic(String topicId) {
        return new AutoValue_TopicsKey(topicId);
    }

    @Override
    public String stackIdentifier() {
        return MainActivity.StackType.TOPICS.name();
    }

    @Override
    public String getFragmentTag() {
        return "TopicsKey";
    }

    @Override
    public Fragment createFragment() {
        return TopicsFragment.newInstance(topicId());
    }
}
