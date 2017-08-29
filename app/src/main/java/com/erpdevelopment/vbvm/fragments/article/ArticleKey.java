package com.erpdevelopment.vbvm.fragments.article;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.erpdevelopment.vbvm.application.Key;
import com.erpdevelopment.vbvm.application.MainActivity;
import com.google.auto.value.AutoValue;

/**
 * Created by thomascarey on 29/08/17.
 */

@AutoValue
public abstract class ArticleKey extends Key {

    abstract String articleId();

    public static ArticleKey create(String articleId) {
        return new AutoValue_ArticleKey(articleId);
    }

    @Override
    public String stackIdentifier() {
        return MainActivity.StackType.TOPICS.name();
    }

    @Override
    public String getFragmentTag() {
        return "ArticleKey";
    }

    @Override
    public Fragment createFragment() {
        return ArticleFragment.newInstance(articleId());
    }
}
