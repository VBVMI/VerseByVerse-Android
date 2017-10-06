package com.erpdevelopment.vbvm;

import android.content.Context;
import android.net.Uri;

import com.erpdevelopment.vbvm.model.Lesson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomascarey on 12/08/17.
 */

public class FileHelpers {

    public static final String FILE_AUDIO = "FILE_AUDIO";
    public static final String FILE_HANDOUT = "FILE_HANDOUT";
    public static final String FILE_TRANSCRIPT = "FILE_TRANSCRIPT";
    public static final String FILE_SLIDES = "FILE_SLIDES";

    public static String relativePath(Lesson lesson, String type) {
        return relativePathForSource(lesson, sourceForType(lesson, type));
    }

    public static String sourceForType(Lesson lesson, String type) {
        String source = null;
        switch (type) {
            case FILE_AUDIO: {
                source = lesson.audioSource;
                break;
            }
            case FILE_HANDOUT: {
                source = lesson.studentAidSource;
                break;
            }
            case FILE_TRANSCRIPT: {
                source = lesson.transcriptSource;
                break;
            }
            case  FILE_SLIDES: {
                source = lesson.teacherAidSource;
                break;
            }
        }

        if (source == null || source.isEmpty() || !source.startsWith("http")) {
            return null;
        }
        return source;
    }

    private static String relativePathForSource(Lesson lesson, String sourceString) {
        if (sourceString == null) {
            return null;
        }
        Uri source = Uri.parse(sourceString);
        if (source == null) {
            return null;
        }
        String name = source.getLastPathSegment();
        return "lessons/" + lesson.id + "/" + name;
    }

    public static Uri filePathForType(Context context, Lesson lesson, String type) {
        File directory = context.getExternalFilesDir("Documents");
        if (directory == null) {
            return null;
        }
        Uri basePath = Uri.fromFile(directory);
        return Uri.withAppendedPath(basePath, relativePath(lesson, type));
    }

    public static File fileForType(Context context, Lesson lesson, String type) {
        Uri path = filePathForType(context, lesson, type);
        if (path == null) {
            return null;
        }
        File file = new File(path.getPath());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }

    public static String relativeAudioPath(Lesson lesson) {
        return relativePath(lesson, FILE_AUDIO);
    }

    public static Uri audioFilePathForLesson(Context context, Lesson lesson) {
        return filePathForType(context, lesson, FILE_AUDIO);
    }

    public static File getAudioFileForLesson(Context context, Lesson lesson) {
        return fileForType(context, lesson, FILE_AUDIO);
    }

    public static boolean hasDownloadedFiles(Context context, Lesson lesson) {

        String[] types = new String[]{FILE_AUDIO, FILE_HANDOUT, FILE_SLIDES, FILE_TRANSCRIPT};
        for (String type : types) {
            File file = fileForType(context, lesson, type);
            if (file.exists()) {
                return true;
            }
        }

        return false;
    }

    public static boolean deleteAllFiles(Context context, Lesson lesson) {
        String[] types = new String[]{FILE_AUDIO, FILE_HANDOUT, FILE_SLIDES, FILE_TRANSCRIPT};
        boolean success = true;
        for (String type : types) {
            File file = fileForType(context, lesson, type);
            if (file.exists()) {
                success = success & file.delete();
            }
        }
        return success;
    }

}
