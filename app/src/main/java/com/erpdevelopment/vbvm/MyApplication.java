package com.erpdevelopment.vbvm;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.erpdevelopment.vbvm.database.AppDatabase;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import com.erpdevelopment.vbvm.api.APIManager;
import com.raizlabs.android.dbflow.runtime.ContentResolverNotifier;
import com.vimeo.networking.Configuration;
import com.vimeo.networking.Vimeo;
import com.vimeo.networking.VimeoClient;

import androidx.multidex.MultiDexApplication;

/**
 * Created by thomascarey on 28/06/17.
 */

public class MyApplication extends MultiDexApplication {

    private static final String TAG = "MyApplication";
    private static final boolean IS_DEBUG_BUILD = true;

    @Override
    public void onCreate() {
        FlowManager.init(new FlowConfig.Builder(this)
                .addDatabaseConfig(new DatabaseConfig.Builder(AppDatabase.class)
                        .databaseName("AppDatabase")
                        .modelNotifier(new ContentResolverNotifier(BuildConfig.APPLICATION_ID))
                        .build())
                .build());

        super.onCreate();

        LessonResourceManager.getInstance().setup(this);
        APIManager.configureAPIManager(this);

        registerVimeo();

    }


    private void registerVimeo() {

        String accessToken = getString(R.string.vimeo_access_token);
        Configuration.Builder configBuilder = new Configuration.Builder(accessToken);
        if (IS_DEBUG_BUILD) {
            // Disable cert pinning if debugging (so we can intercept packets)
            configBuilder.enableCertPinning(false);
            configBuilder.setLogLevel(Vimeo.LogLevel.VERBOSE);
        }
        VimeoClient.initialize(configBuilder.build());
    }

    public static String getUserAgentString(Context context) {
        String packageName = context.getPackageName();

        String version = "unknown";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            System.out.println("Unable to get packageInfo: " + e.getMessage());
        }

        String deviceManufacturer = Build.MANUFACTURER;
        String deviceModel = Build.MODEL;
        String deviceBrand = Build.BRAND;

        String versionString = Build.VERSION.RELEASE;
        String versionSDKString = String.valueOf(Build.VERSION.SDK_INT);

        return packageName + " (" + deviceManufacturer + ", " + deviceModel + ", " + deviceBrand +
                ", " + "Android " + versionString + "/" + versionSDKString + " Version " + version +
                ")";
    }

}
