package org.versebyverseministry.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

/**
 * Created by thomascarey on 9/09/17.
 */

@Table(database = AppDatabase.class)
public class Channel extends BaseModel implements Mergable<Channel> {

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
    @SerializedName("thumbnailSource")
    @Expose
    public String thumbnailSource;

    @Column
    @SerializedName("url")
    @Expose
    public String url;

    @Column
    public int videoCount;

    @SerializedName("videos")
    @Expose
    public List<Video> videos;

    public List<Video> fetchVideos() {
        if (videos == null || videos.isEmpty()) {
            videos = SQLite.select().from(Video.class)
                    .where(Video_Table.channelId.eq(id))
                    .queryList();
        }
        return videos;
    }

    @Override
    public String identifier() {
        return id;
    }

    @Override
    public void mergeAPIAttributes(Channel apiVersion) {

        if (!this.id.equals(apiVersion.id))
            throw new IllegalArgumentException("The apiVersion.id must match the current id");

        this.postedDate = apiVersion.postedDate;
        this.title = apiVersion.title;
        this.thumbnailSource = apiVersion.thumbnailSource;
        this.url = apiVersion.url;
        this.videoCount = apiVersion.videoCount;
    }

    public static String updated() {
        return "UPDATED-Channel";
    }
}
