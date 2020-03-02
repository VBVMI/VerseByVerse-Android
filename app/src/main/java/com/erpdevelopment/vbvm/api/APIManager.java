package com.erpdevelopment.vbvm.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.util.Log;

import com.erpdevelopment.vbvm.model.Study;
import org.versebyverseministry.models.pojo.Answers;
import org.versebyverseministry.models.pojo.Channels;
import org.versebyverseministry.models.pojo.GroupStudies;
import org.versebyverseministry.models.pojo.Lessons;
import org.versebyverseministry.models.pojo.Studies;
import com.erpdevelopment.vbvm.model.Category;
import com.erpdevelopment.vbvm.model.Lesson;
import org.versebyverseministry.models.pojo.Articles;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by thomascarey on 8/06/17
 */

public class APIManager {
    private static final APIManager ourInstance = new APIManager();

    public static APIManager getInstance() {
        return ourInstance;
    }

    private final Handler mainHandler = new Handler();

    private APIInterface apiInterface;

    private ConnectivityManager connectivityManager = null;

    public static void configureAPIManager(Context context) {
        getInstance().connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private APIManager() {
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }

    private boolean isOnWifi() {
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI && info.isConnected()) {
            return true;
        }
        return false;
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
                        if(studies == null || studies.getStudies() == null) {
                            mainHandler.post(() -> {
                                callback.didComplete(false);
                            });
                            return;
                        }
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
                        if(categories == null) {
                            mainHandler.post(() -> {
                                callback.didComplete(false);
                            });
                            return;
                        }
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
                        if(lessons == null || lessons.getLessons() == null) {
                            mainHandler.post(() -> {
                                callback.didComplete(false);
                            });
                            return;
                        }
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

    public void downloadArticles(RequestComplete callback) {

        boolean fullDownload = isOnWifi();
        Call<Articles> call = fullDownload ? apiInterface.doGetArticles() : apiInterface.doGetLatestArticles();

        call.enqueue(new Callback<Articles>() {
            @Override
            public void onResponse(Call<Articles> call, Response<Articles> response) {
                AsyncTask.execute(() -> {
                    Articles articles = response.body();
                    if(articles == null || articles.getArticles() == null) {
                        mainHandler.post(() -> {
                            callback.didComplete(false);
                        });
                        return;
                    }
                    DatabaseManager.getInstance().saveArticles(articles.getArticles(), fullDownload);
                    mainHandler.post(() -> {
                        callback.didComplete(true);
                    });
                });
            }

            @Override
            public void onFailure(Call<Articles> call, Throwable t) {
                call.cancel();
                mainHandler.post(() -> {
                    callback.didComplete(false);
                });
            }
        });

    }

    public void downloadAnswers(RequestComplete callback) {

        boolean fullDownload = isOnWifi();
        Call<Answers> call = fullDownload ? apiInterface.doGetAnswers() : apiInterface.doGetLatestAnswers();

        call.enqueue(new Callback<Answers>() {
            @Override
            public void onResponse(Call<Answers> call, Response<Answers> response) {
                AsyncTask.execute(() -> {
                    Answers answers = response.body();
                    if(answers == null || answers.getAnswers() == null) {
                        mainHandler.post(() -> {
                            callback.didComplete(false);
                        });
                        return;
                    }
                    DatabaseManager.getInstance().saveAnswers(answers.getAnswers(), fullDownload);
                    mainHandler.post(() -> {
                        callback.didComplete(true);
                    });
                });
            }

            @Override
            public void onFailure(Call<Answers> call, Throwable t) {
                call.cancel();
                mainHandler.post(() -> {
                    callback.didComplete(false);
                });
            }
        });

    }

    public void downloadChannels(RequestComplete callback) {

        Call<Channels> call = apiInterface.doGetChannels();
        call.enqueue(new Callback<Channels>() {
            @Override
            public void onResponse(Call<Channels> call, Response<Channels> response) {
                AsyncTask.execute(() -> {
                    Channels channels = response.body();
                    if(channels == null || channels.getChannels() == null) {
                        mainHandler.post(() -> {
                            callback.didComplete(false);
                        });
                        return;
                    }
                    DatabaseManager.getInstance().saveChannels(channels.getChannels());
                    if(callback != null) {
                        mainHandler.post(() -> {
                            callback.didComplete(true);
                        });
                    }
                });
            }

            @Override
            public void onFailure(Call<Channels> call, Throwable t) {
                call.cancel();
                if (callback != null) {
                    mainHandler.post(() -> {
                        callback.didComplete(false);
                    });
                }
            }
        });

    }

    public void downloadGroupStudies(RequestComplete callback) {

        Call<GroupStudies> call = apiInterface.doGetGroupStudies();
        call.enqueue(new Callback<GroupStudies>() {
            @Override
            public void onResponse(Call<GroupStudies> call, Response<GroupStudies> response) {
                AsyncTask.execute(() -> {
                    GroupStudies groupStudies = response.body();
                    DatabaseManager.getInstance().saveGroupStudies(groupStudies.getChannels());
                    if(callback != null) {
                        mainHandler.post(() -> {
                            callback.didComplete(true);
                        });
                    }
                });
            }

            @Override
            public void onFailure(Call<GroupStudies> call, Throwable t) {
                call.cancel();
                if (callback != null) {
                    mainHandler.post(() -> {
                        callback.didComplete(false);
                    });
                }
            }
        });

    }

}
