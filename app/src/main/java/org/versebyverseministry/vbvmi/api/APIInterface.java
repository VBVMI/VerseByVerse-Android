package org.versebyverseministry.vbvmi.api;

import android.support.annotation.NonNull;

import org.versebyverseministry.vbvmi.model.Category;
import org.versebyverseministry.vbvmi.model.pojo.Lessons;
import org.versebyverseministry.vbvmi.model.pojo.Studies;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by thomascarey on 8/06/17.
 */

public interface APIInterface {

    @GET("/corev2/json")
    Call<Studies> doGetStudies();

    @GET("/corev2/categories")
    Call<List<Category>> doGetCategories();

    @GET("/corev2/json-lessons/{id}")
    Call<Lessons> doGetLessons(@NonNull @Path("id") String studyId);
}
