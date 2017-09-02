package com.erpdevelopment.vbvm.model.pojo;

import com.erpdevelopment.vbvm.model.Article;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
