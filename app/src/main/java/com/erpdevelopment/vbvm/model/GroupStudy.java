package com.erpdevelopment.vbvm.model;

import com.erpdevelopment.vbvm.api.Mergable;
import com.erpdevelopment.vbvm.database.AppDatabase;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by thomascarey on 9/09/17.
 */

@Table(database = AppDatabase.class)
@Parcel(analyze = {GroupStudy.class})
public class GroupStudy extends BaseModel implements Mergable<GroupStudy> {

    @PrimaryKey
    @SerializedName("ID")
    @Expose
    public String id;

    @Column
    @SerializedName("postedDate")
    @Expose
    public Long postedDate;

    @Column
    @SerializedName("title")
    @Expose
    public String title;

    @Column
    @SerializedName("pdf")
    @Expose
    public String pdf;

    @Column
    @SerializedName("cover_image")
    @Expose
    public String coverImage;

    @Column
    @SerializedName("url")
    @Expose
    public String url;

    @SerializedName("videos")
    @Expose
    public List<Video> videos;

    @Column
    public int videoCount;

    public List<Video> fetchVideos() {
        if (videos == null || videos.isEmpty()) {
            videos = SQLite.select().from(Video.class)
                    .where(Video_Table.groupStudyId.eq(id))
                    .orderBy(Video_Table.id, true)
                    .queryList();
        }
        return videos;
    }


    @Override
    public String identifier() {
        return id;
    }

    @Override
    public void mergeAPIAttributes(GroupStudy apiVersion) {

        if (!this.id.equals(apiVersion.id))
            throw new IllegalArgumentException("The apiVersion.id must match the current id");

        this.postedDate = apiVersion.postedDate;
        this.title = apiVersion.title;
        this.pdf = apiVersion.pdf;
        this.coverImage = apiVersion.coverImage;
        this.url = apiVersion.url;
        this.videoCount = apiVersion.videoCount;
    }

    public static String updated() {
        return "UPDATED-GroupStudy";
    }
}
