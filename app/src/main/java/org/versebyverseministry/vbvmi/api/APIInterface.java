package org.versebyverseministry.vbvmi.api;

import org.versebyverseministry.vbvmi.model.pojo.Studies;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;

/**
 * Created by thomascarey on 8/06/17.
 */

public interface APIInterface {

    @GET("/corev2/json")
    Call<Studies> doGetStudies();

}
