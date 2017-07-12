package org.versebyverseministry.vbvmi.application;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;

import com.zhuinden.simplestack.BackstackDelegate;

import org.versebyverseministry.vbvmi.util.ServiceLocator;

/**
 * Created by Owner on 2017. 01. 12..
 */

public abstract class Key
        implements Parcelable {

    @LayoutRes
    public abstract int layout();

    public final BackstackDelegate selectDelegate(Context context) {
        return ServiceLocator.getService(context, stackIdentifier());
    }

    public abstract String stackIdentifier();
}
