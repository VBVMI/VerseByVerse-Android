package com.erpdevelopment.vbvm.model;

import com.erpdevelopment.vbvm.api.Mergable;
import com.erpdevelopment.vbvm.database.AppDatabase;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ManyToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by thomascarey on 2/09/17.
 */

@Table(database = AppDatabase.class)
@ManyToMany(referencedTable = Topic.class)
@Parcel(analyze = {Answer.class})
public class Answer extends BaseModel implements Mergable<Answer> {
    @PrimaryKey
    @SerializedName("ID")
    @Expose
    public String id;

    @Column
    @SerializedName("category")
    @Expose
    public String category;

    @SerializedName("topics")
    @Expose
    public List<Topic> topics = null;

    @Column
    @SerializedName("postedDate")
    @Expose
    public Long postedDate;

    @Column
    @SerializedName("title")
    @Expose
    public String title;

    @Column
    @SerializedName("authorThumbnailSource")
    @Expose
    public String authorThumbnailSource;

    @Column
    @SerializedName("authorThumbnailAltText")
    @Expose
    public String authorThumbnailAltText;

    @Column
    @SerializedName("authorName")
    @Expose
    public String authorName;

    @Column
    @SerializedName("url")
    @Expose
    public String url;

    @Column
    @SerializedName("body")
    @Expose
    public String body;

    @Column
    public boolean hasRead = false;

    @Override
    public String identifier() {
        return id;
    }

    @Override
    public void mergeAPIAttributes(Answer apiVersion) {
        if (!this.id.equals(apiVersion.id))
            throw new IllegalArgumentException("The apiVersion.id must match the current id");

        this.category = apiVersion.category;
        this.topics = apiVersion.topics;
        this.postedDate = apiVersion.postedDate;
        this.title = apiVersion.title;
        this.authorThumbnailSource = apiVersion.authorThumbnailSource;
        this.authorThumbnailAltText = apiVersion.authorThumbnailAltText;
        this.authorName = apiVersion.authorName;
        this.url = apiVersion.url;
        this.body = apiVersion.body;
    }

    public static String updated() {
        return "UPDATED-Answer";
    }
}
