package com.erpdevelopment.vbvm

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.multidex.MultiDexApplication
import com.erpdevelopment.vbvm.api.APIManager
import com.raizlabs.android.dbflow.config.DatabaseConfig
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.runtime.ContentResolverNotifier
import com.vimeo.networking.Configuration
import com.vimeo.networking.Vimeo
import com.vimeo.networking.VimeoClient
import org.versebyverseministry.models.AppDatabase

/**
 * Created by thomascarey on 28/06/17.
 */
class MyApplication : MultiDexApplication() {
    override fun onCreate() {
        FlowManager.init(FlowConfig.Builder(this)
                .addDatabaseConfig(DatabaseConfig.Builder(AppDatabase::class.java)
                        .databaseName("AppDatabase")
                        .modelNotifier(ContentResolverNotifier(BuildConfig.APPLICATION_ID))
                        .build())
                .build())
        super.onCreate()
        LessonResourceManager.getInstance().setup(this)
        APIManager.configureAPIManager(this)
        registerVimeo()
    }

    private fun registerVimeo() {
        val accessToken = getString(R.string.vimeo_access_token)
        val configBuilder = Configuration.Builder(accessToken)
        if (IS_DEBUG_BUILD) {
            // Disable cert pinning if debugging (so we can intercept packets)
            configBuilder.enableCertPinning(false)
            configBuilder.setLogLevel(Vimeo.LogLevel.VERBOSE)
        }
        VimeoClient.initialize(configBuilder.build())
    }

    companion object {
        private const val TAG = "MyApplication"
        private const val IS_DEBUG_BUILD = true
        fun getUserAgentString(context: Context): String {
            val packageName = context.packageName
            var version = "unknown"
            try {
                val pInfo = context.packageManager.getPackageInfo(packageName, 0)
                version = pInfo.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                println("Unable to get packageInfo: " + e.message)
            }
            val deviceManufacturer = Build.MANUFACTURER
            val deviceModel = Build.MODEL
            val deviceBrand = Build.BRAND
            val versionString = Build.VERSION.RELEASE
            val versionSDKString = Build.VERSION.SDK_INT.toString()
            return packageName + " (" + deviceManufacturer + ", " + deviceModel + ", " + deviceBrand +
                    ", " + "Android " + versionString + "/" + versionSDKString + " Version " + version +
                    ")"
        }
    }
}