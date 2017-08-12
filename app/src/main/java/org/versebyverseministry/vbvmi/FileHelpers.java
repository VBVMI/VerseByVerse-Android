package org.versebyverseministry.vbvmi;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import org.versebyverseministry.vbvmi.model.Lesson;

import java.io.File;

/**
 * Created by thomascarey on 12/08/17.
 */

public class FileHelpers {

    public static String relativeAudioPath(Lesson lesson) {
        Uri audioSource = Uri.parse(lesson.audioSource);
        String audioName = audioSource.getLastPathSegment();
        return "lessons/" + audioName;
    }

    public static Uri audioFilePathForLesson(Context context, Lesson lesson) {
        File directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        Uri basePath = Uri.fromFile(directory);
        return Uri.withAppendedPath(basePath, relativeAudioPath(lesson));
    }

    public static File getAudioFileForLesson(Context context, Lesson lesson) {
        File file = new File(audioFilePathForLesson(context, lesson).getPath());
        return file;
    }
}
