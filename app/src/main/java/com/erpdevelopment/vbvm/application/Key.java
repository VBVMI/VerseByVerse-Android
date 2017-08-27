package com.erpdevelopment.vbvm.application;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.erpdevelopment.vbvm.util.ServiceLocator;
import com.zhuinden.simplestack.BackstackDelegate;

/**
 * Created by Owner on 2017. 01. 12..
 */

public abstract class Key
        implements Parcelable {

    public final BackstackDelegate selectDelegate(Context context) {
        return ServiceLocator.getService(context, stackIdentifier());
    }

    public abstract String stackIdentifier();

    public abstract String getFragmentTag();

    public abstract Fragment createFragment();
}
