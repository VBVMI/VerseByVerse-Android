package org.versebyverseministry.vbvmi.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.versebyverseministry.vbvmi.model.Article;

import java.util.List;

/**
 * Created by thomascarey on 26/08/17.
 */

public class Articles {
    @SerializedName("articles")
    @Expose
    private List<Article> lessons = null;

    public List<Article> getLessons() { return lessons; }

    public void setLessons(List<Article> lessons) {
        this.lessons = lessons;
    }
}
