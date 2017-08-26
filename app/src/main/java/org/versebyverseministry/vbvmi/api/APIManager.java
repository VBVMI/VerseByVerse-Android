package org.versebyverseministry.vbvmi.api;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import org.versebyverseministry.vbvmi.model.Category;
import org.versebyverseministry.vbvmi.model.Lesson;
import org.versebyverseministry.vbvmi.model.Study;
import org.versebyverseministry.vbvmi.model.pojo.Lessons;
import org.versebyverseministry.vbvmi.model.pojo.Studies;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomascarey on 8/06/17.
 */

public class APIManager {
    private static final APIManager ourInstance = new APIManager();

    public static APIManager getInstance() {
        return ourInstance;
    }

    private final Handler mainHandler = new Handler();

    private APIInterface apiInterface;

    private APIManager() {
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    public interface RequestComplete {
        void didComplete(boolean success);
    }

    public void downloadStudies(RequestComplete callback) {

        Call<Studies> call = apiInterface.doGetStudies();

        call.enqueue(new Callback<Studies>() {
            @Override
            public void onResponse(Call<Studies> call, final Response<Studies> response) {
                Log.d("VBVMI", "get all studies: " + response.code());
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Studies studies = response.body();
                        List<Study> studyList = studies.getStudies();
                        DatabaseManager.getInstance().saveStudies(studyList);
                        mainHandler.post(() -> {
                           callback.didComplete(true);
                        });
                    }
                });

            }

            @Override
            public void onFailure(Call<Studies> call, Throwable t) {
                call.cancel();
                mainHandler.post(() -> {
                    callback.didComplete(false);
                });
            }
        });

    }

    public void downloadCategories(RequestComplete callback) {

        Call<List<Category>> call = apiInterface.doGetCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, final Response<List<Category>> response) {
                Log.d("VBVMI", "get all categories: " + response.code());

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<Category> categories = response.body();
                        DatabaseManager.getInstance().saveCategories(categories);
                        mainHandler.post(() -> {
                            callback.didComplete(true);
                        });
                    }
                });

            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                call.cancel();
                mainHandler.post(() -> {
                    callback.didComplete(false);
                });
            }
        });

    }

    public void downloadLessons(@NonNull final String studyId, RequestComplete callback) {

        Call<Lessons> call = apiInterface.doGetLessons(studyId);
        call.enqueue(new Callback<Lessons>() {
            @Override
            public void onResponse(Call<Lessons> call, final Response<Lessons> response) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Lessons lessons = response.body();
                        List<Lesson> lessonList = lessons.getLessons();
                        DatabaseManager.getInstance().saveLessons(lessonList, studyId);
                        mainHandler.post(() -> {
                            callback.didComplete(true);
                        });
                    }
                });
            }

            @Override
            public void onFailure(Call<Lessons> call, Throwable t) {
                call.cancel();
                mainHandler.post(() -> {
                    callback.didComplete(false);
                });
            }
        });

    }

}
