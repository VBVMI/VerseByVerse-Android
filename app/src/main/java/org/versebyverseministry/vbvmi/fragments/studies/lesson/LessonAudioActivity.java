package org.versebyverseministry.vbvmi.fragments.studies.lesson;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.versebyverseministry.vbvmi.PlayAudio;
import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.model.Lesson;
import org.versebyverseministry.vbvmi.model.Lesson_Table;
import org.versebyverseministry.vbvmi.model.Study;
import org.versebyverseministry.vbvmi.model.Study_Table;

import java.io.File;
import java.net.URI;

import berlin.volders.rxdownload.RxDownloadManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

public class LessonAudioActivity extends AppCompatActivity {

    private static String TAG = "LessonAudioActivity";

    public static String ARG_LESSON_ID = "ARG_LESSON_ID";


    private Lesson lesson;
    private Study study;

    @BindView(R.id.imageView)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_audio);

        String lessonId = getIntent().getExtras().getString(ARG_LESSON_ID);
        lesson = SQLite.select().from(Lesson.class).where(Lesson_Table.id.eq(lessonId)).querySingle();
        study = SQLite.select().from(Study.class).where(Study_Table.id.eq(lesson.studyId)).querySingle();

        ButterKnife.bind(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

        int imageWidth = displayMetrics.widthPixels;
        String studyImageURL = study.imageForWidth(imageWidth);
        if (studyImageURL != null) {
            Glide.with(this).load(studyImageURL).into(imageView);
        } else {
            Glide.with(this).load(study.thumbnailSource).into(imageView);
        }

//        Intent audioIntent = new Intent(this, PlayAudio.class);
//        startService(audioIntent);

//        Notification notification = new NotificationCompat.Builder(this)
//                .setContentTitle(lesson.description)
//                .setContentText(lesson.lessonNumber)
//                .setStyle(new NotificationCompat.MediaStyle())
//                .build();
//
//        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0, notification);


//        Uri audioSource = Uri.parse(lesson.audioSource);
//
//        String lastPathComponent = study.id + "_" + lesson.id + "_" + audioSource.getLastPathSegment();
//
//        DownloadManager.Request request = RxDownloadManager.request(Uri.parse(lesson.audioSource), lastPathComponent)
//                .setDescription("Downloading...")
//                .setDestinationInExternalFilesDir(this, Environment.DIRECTORY_PODCASTS, "Audio/" + lastPathComponent);
        File downloadsFile = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
//        //uri:file:///storage/emulated/10/Android/data/org.versebyverseministry.vbvmi/files/Podcasts/VBVMI-1
//        //uri:file:///storage/emulated/0/Android/data/org.versebyverseministry.vbvmi/files/Podcasts/Audio/1420_1443_john1a.mp3
//        File download = getExternalFilesDir(Environment.DIRECTORY_PODCASTS);
//        String path = download.getPath();
//        String testPath = path + "/Audio/" + lastPathComponent;
//        File testFile = new File(testPath);
//        if (!testFile.exists()) {
//            RxDownloadManager rxDownloadManager = RxDownloadManager.from(this);
//            rxDownloadManager.download(request).subscribe(new Subscriber<Uri>() {
//                @Override
//                public void onCompleted() {
//
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    Log.d(TAG, "onError: e:" + e.toString());
//                }
//
//                @Override
//                public void onNext(Uri uri) {
//                    Log.d(TAG, "onNext: uri:" + uri.toString());
//                }
//            });
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent audioIntent = new Intent(this, PlayAudio.class);
        stopService(audioIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }
}
