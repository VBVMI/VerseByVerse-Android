package org.versebyverseministry.models.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.versebyverseministry.models.Article;

import java.util.List;

/**
 * Created by thomascarey on 26/08/17.
 */

public class Articles {
    @SerializedName("articles")
    @Expose
    private List<Article> articles = null;

    public List<Article> getArticles() { return articles; }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}
