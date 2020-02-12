package com.erpdevelopment.vbvm.model;

import androidx.annotation.Nullable;

import com.erpdevelopment.vbvm.database.AppDatabase;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ManyToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;
import com.erpdevelopment.vbvm.api.Mergable;

import java.util.List;

/**
 * Created by thomascarey on 8/06/17.
 */

@Table(database = AppDatabase.class)
@ManyToMany(referencedTable = Topic.class)
@Parcel(analyze = {Study.class})
public class Study extends BaseModel implements Mergable<Study> {

    @SerializedName("topics")
    @Expose
    public List<Topic> topics = null;

    @Column
    @SerializedName("bibleIndex")
    @Expose
    public Integer bibleIndex;

    @Column
    @SerializedName("thumbnailSource")
    @Expose
    public String thumbnailSource;

    @Column
    @SerializedName("title")
    @Expose
    public String title;

    @Column
    @SerializedName("podcastLink")
    @Expose
    public String podcastLink;

    @Column
    @SerializedName("lessonCount")
    @Expose
    public Integer lessonCount;

    @Column
    @SerializedName("description")
    @Expose
    public String description;

    @Column
    @SerializedName("category")
    @Expose
    public Integer category;

    @PrimaryKey
    @SerializedName("ID")
    @Expose
    public String id;

    @Column
    @SerializedName("url")
    @Expose
    public String url;

    @Column
    @SerializedName("image160")
    @Expose
    public String image160;

    @Column
    @SerializedName("image300")
    @Expose
    public String image300;

    @Column
    @SerializedName("image600")
    @Expose
    public String image600;

    @Column
    @SerializedName("image900")
    @Expose
    public String image900;

    @Column
    @SerializedName("image1100")
    @Expose
    public String image1100;

    @Column
    @SerializedName("image1400")
    @Expose
    public String image1400;


    @Override
    public String identifier() {
        return id;
    }

    @Nullable
    public String imageForWidth(int width) {
        if (width > 1100) {
            return image1400;
        } else if (width > 900) {
            return image1100;
        } else if (width > 600) {
            return image900;
        } else if (width > 300) {
            return image600;
        } else if (width > 160) {
            return image300;
        } else {
            return image160;
        }
    }

    @Override
    public void mergeAPIAttributes(Study apiVersion) {
        if (!this.id.equals(apiVersion.id))
            throw new IllegalArgumentException("The apiVersion.id must match the current id");

        // Only merge data that is exposed to GSON
        this.bibleIndex = apiVersion.bibleIndex;
        this.thumbnailSource = apiVersion.thumbnailSource;
        this.title = apiVersion.title;
        this.podcastLink = apiVersion.podcastLink;
        this.lessonCount = apiVersion.lessonCount;
        this.description = apiVersion.description;
        this.category = apiVersion.category;
        this.url = apiVersion.url;
        this.topics = apiVersion.topics;
        this.image160 = apiVersion.image160;
        this.image300 = apiVersion.image300;
        this.image600 = apiVersion.image600;
        this.image900 = apiVersion.image900;
        this.image1100 = apiVersion.image1100;
        this.image1400 = apiVersion.image1400;
    }

    public static String updated() {
        return "UPDATED-Study";
    }
}
