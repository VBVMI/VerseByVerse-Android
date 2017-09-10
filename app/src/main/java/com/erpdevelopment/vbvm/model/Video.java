package com.erpdevelopment.vbvm.model;

import com.erpdevelopment.vbvm.api.Mergable;
import com.erpdevelopment.vbvm.database.AppDatabase;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

/**
 * Created by thomascarey on 9/09/17.
 */

@Table(database = AppDatabase.class)
@Parcel(analyze = {Video.class})
public class Video extends BaseModel implements Mergable<Video> {

    @PrimaryKey
    @SerializedName("ID")
    @Expose
    public String id;

    @Column
    public String channelId;

    @Column
    public String groupStudyId;

    @Column
    @SerializedName("title")
    @Expose
    public String title;

    @Column
    @SerializedName("description")
    @Expose
    public String description;

    @Column
    @SerializedName("recordedDate")
    @Expose
    public String recordedDate;

    @Column
    @SerializedName("serviceVideoID")
    @Expose
    public String serviceVideoId;

    @Column
    @SerializedName("service")
    @Expose
    public String service;

    @Column
    @SerializedName("url")
    @Expose
    public String url;

    @Column
    @SerializedName("videoSource")
    @Expose
    public String videoSource;

    @Column
    @SerializedName("videoLength")
    @Expose
    public String videoLength;

    @Column
    @SerializedName("thumbnailSource")
    @Expose
    public String thumbnailSource;

    @Column
    public boolean watched;

    @Override
    public String identifier() {
        return id;
    }

    @Override
    public void mergeAPIAttributes(Video apiVersion) {
        if (!this.id.equals(apiVersion.id))
            throw new IllegalArgumentException("The apiVersion.id must match the current id");

        this.title = apiVersion.title;
        this.description = apiVersion.description;
        this.recordedDate = apiVersion.recordedDate;
        this.serviceVideoId = apiVersion.serviceVideoId;
        this.service = apiVersion.service;
        this.url = apiVersion.url;
        this.videoSource = apiVersion.videoSource;
        this.videoLength = apiVersion.videoLength;
        this.thumbnailSource = apiVersion.thumbnailSource;
        this.channelId = apiVersion.channelId;
        this.groupStudyId = apiVersion.groupStudyId;
    }
}
