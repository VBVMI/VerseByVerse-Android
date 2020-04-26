package com.erpdevelopment.vbvm.api;

import androidx.annotation.NonNull;

import org.versebyverseministry.models.Category;
import org.versebyverseministry.models.pojo.Answers;
import org.versebyverseministry.models.pojo.Articles;
import org.versebyverseministry.models.pojo.Channels;
import org.versebyverseministry.models.pojo.GroupStudies;
import org.versebyverseministry.models.pojo.Lessons;
import org.versebyverseministry.models.pojo.Studies;

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

    @GET("/corev2/json-channels")
    Call<Channels> doGetChannels();

    @GET("/corev2/json-curriculum")
    Call<GroupStudies> doGetGroupStudies();
}
