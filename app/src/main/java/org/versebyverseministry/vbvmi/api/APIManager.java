package org.versebyverseministry.vbvmi.api;

import android.os.AsyncTask;
import android.util.Log;

import org.versebyverseministry.vbvmi.model.Study;
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


    private APIInterface apiInterface;

    private APIManager() {
        apiInterface = APIClient.getClient().create(APIInterface.class);
    }


    public void downloadStudies() {

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
                    }
                });

            }

            @Override
            public void onFailure(Call<Studies> call, Throwable t) {
                call.cancel();
            }
        });

    }



}
