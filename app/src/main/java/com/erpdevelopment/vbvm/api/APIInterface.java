package com.erpdevelopment.vbvm.api;

import android.support.annotation.NonNull;

import com.erpdevelopment.vbvm.model.Category;
import com.erpdevelopment.vbvm.model.pojo.Answers;
import com.erpdevelopment.vbvm.model.pojo.Articles;
import com.erpdevelopment.vbvm.model.pojo.Lessons;
import com.erpdevelopment.vbvm.model.pojo.Studies;

import java.util.List;

import retrofit2.Call;
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

    @GET("/corev2/json-articles")
    Call<Articles> doGetArticles();

    @GET("/corev2/json-articlesp")
    Call<Articles> doGetLatestArticles();

    @GET("/corev2/json-qa")
    Call<Answers> doGetAnswers();

    @GET("/corev2/json-qap")
    Call<Answers> doGetLatestAnswers();
}
