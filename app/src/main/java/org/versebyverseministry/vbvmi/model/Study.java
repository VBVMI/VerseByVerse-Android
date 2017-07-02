package org.versebyverseministry.vbvmi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ManyToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;
import org.versebyverseministry.vbvmi.api.Mergable;
import org.versebyverseministry.vbvmi.database.AppDatabase;

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
    @SerializedName("thumbnailAltText")
    @Expose
    public String thumbnailAltText;

    @Column
    @SerializedName("podcastLink")
    @Expose
    public String podcastLink;

    @Column
    @SerializedName("lessonCount")
    @Expose
    public Integer lessonCount;

    @Column
    @SerializedName("averageRating")
    @Expose
    public String averageRating;

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

    @Override
    public String identifier() {
        return id;
    }

    @Override
    public void mergeAPIAttributes(Study apiVersion) {
        if (!this.id.equals(apiVersion.id))
            throw new IllegalArgumentException("The apiVersion.id must match the current id");

        // Only merge data that is exposed to GSON
        this.bibleIndex = apiVersion.bibleIndex;
        this.thumbnailSource = apiVersion.thumbnailSource;
        this.title = apiVersion.title;
        this.thumbnailAltText = apiVersion.thumbnailAltText;
        this.podcastLink = apiVersion.podcastLink;
        this.lessonCount = apiVersion.lessonCount;
        this.averageRating = apiVersion.averageRating;
        this.description = apiVersion.description;
        this.category = apiVersion.category;
        this.url = apiVersion.url;
        this.topics = apiVersion.topics;
    }
}
