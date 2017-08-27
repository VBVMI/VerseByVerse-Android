package com.erpdevelopment.vbvm;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.erpdevelopment.vbvm.model.Lesson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thomascarey on 22/08/17.
 */

public class LessonResourceManager {

    private static String TAG = "LessonResourceManager";
    private static final LessonResourceManager ourInstance = new LessonResourceManager();

    public static LessonResourceManager getInstance() {
        return ourInstance;
    }

    private LessonResourceManager() {
    }

    private DownloadManager dm;

    private Context context;

    public void setup(Context context) {
        this.context = context;
        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        context.registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public interface DownloadCallback {
        void downloadComplete(boolean success, String fileUri);
    }

    private Map<Long, DownloadCallback> queuedCompletions = new HashMap<>();

    private class ResourceKey {
        private String lessonID;
        private String fileType;

        ResourceKey(String lessonId, String fileType) {
            this.lessonID = lessonId;
            this.fileType = fileType;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (! (obj instanceof ResourceKey))
                return false;
            ResourceKey key = (ResourceKey) obj;
            return EqualsUtil.areEqual(key.fileType, fileType) &&
                    EqualsUtil.areEqual(key.lessonID, lessonID);
        }

        @Override
        public String toString() {
            return "Key: [lesson: " + lessonID + ", file: " + fileType + "]";
        }

        @Override
        public int hashCode() {
            return lessonID.hashCode() ^ fileType.hashCode();
        }
    }

    private Map<Long, ResourceKey> resourceKeysByDownloadId = new HashMap<>();

    private Map<ResourceKey, Long> downloadIdsByType = new HashMap<>();

    public boolean isDownloadingResource(String lessonId, String fileType) {
        ResourceKey key = new ResourceKey(lessonId, fileType);
        return downloadIdsByType.containsKey(key);
    }

    public void setDownloadCallback(String lessonId, String fileType, DownloadCallback callback) {
        ResourceKey key = new ResourceKey(lessonId, fileType);
        Long downloadId = downloadIdsByType.get(key);
        if (downloadId != null) {
            queuedCompletions.put(downloadId, callback);
        }
    }

    public void download(Lesson lesson, String fileType, DownloadCallback callback) {
        String source = FileHelpers.sourceForType(lesson, fileType);
        if (source == null || callback == null || source.isEmpty()) {
            return;
        }

        ResourceKey key = new ResourceKey(lesson.id, fileType);
        DownloadCallback myCallback = new DownloadCallback() {
            @Override
            public void downloadComplete(boolean success, String fileUri) {
                if (downloadIdsByType.containsKey(key))
                    downloadIdsByType.remove(key);

                callback.downloadComplete(success, fileUri);
            }
        };

        if (downloadIdsByType.containsKey(key)) {
            long downloadId = downloadIdsByType.get(key);
            queuedCompletions.put(downloadId, myCallback);
            return;
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(source));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        request.setDestinationInExternalFilesDir(this.context, "Documents", FileHelpers.relativePath(lesson, fileType));
        long downloadId = dm.enqueue(request);
        queuedCompletions.put(downloadId, myCallback);
        downloadIdsByType.put(key, downloadId);
        resourceKeysByDownloadId.put(downloadId, key);

    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                // Download was complete
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                if (!queuedCompletions.containsKey(downloadId)) {
                    // this is not the download you're looking for
                    return;
                }
                DownloadCallback callback = queuedCompletions.remove(downloadId);
                Cursor c = dm.query(query);

                ResourceKey key = resourceKeysByDownloadId.remove(downloadId);
                if (key != null) {
                    Log.d(TAG, "attempting to remove " + key);
                    downloadIdsByType.remove(key);
                }

                if (c.moveToFirst()) {
                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    switch (c.getInt(columnIndex)) {
                        case DownloadManager.STATUS_SUCCESSFUL: {

                            int fileUriIndex = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                            String fileUri = c.getString(fileUriIndex);

                            callback.downloadComplete(true, fileUri);
                            return;
                        }
                        case DownloadManager.STATUS_FAILED: {
                            dm.remove(downloadId);
                            break;
                        }
                        default:
                            break;
                    }
                }


                callback.downloadComplete(false, null);

            }
        }
    };
}
