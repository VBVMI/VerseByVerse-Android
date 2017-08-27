package com.erpdevelopment.vbvm;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import com.erpdevelopment.vbvm.api.APIManager;

/**
 * Created by thomascarey on 28/06/17.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FlowManager.init(new FlowConfig.Builder(this).build());
        LessonResourceManager.getInstance().setup(this);
        APIManager.configureAPIManager(this);
    }
}
