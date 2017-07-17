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
 * Created by thomascarey on 17/07/17.
 */

@Table(database = AppDatabase.class)
@ManyToMany(referencedTable = Topic.class)
@Parcel(analyze = {Lesson.class})
public class Lesson extends BaseModel implements Mergable<Lesson> {

    @PrimaryKey
    @SerializedName("ID")
    @Expose
    public String id;

    @Column
    @SerializedName("description")
    @Expose
    public String description;

    @Column
    @SerializedName("postedDate")
    @Expose
    public String postedDate;

    @Column
    @SerializedName("transcript")
    @Expose
    public String transcriptSource;

    @Column
    @SerializedName("lessonNumber")
    @Expose
    public String lessonNumber;

    @Column
    @SerializedName("teacherAid")
    @Expose
    public String teacherAidSource;

    @Column
    @SerializedName("videoSource")
    @Expose
    public String videoSource;

    @Column
    @SerializedName("title")
    @Expose
    public String title;

    @Column
    @SerializedName("audioSource")
    @Expose
    public String audioSource;

    @Column
    @SerializedName("audioLength")
    @Expose
    public String audioLength;

    @Column
    @SerializedName("studentAid")
    @Expose
    public String studentAidSource;

    @Column
    public String studyId;

    @SerializedName("topics")
    @Expose
    public List<Topic> topics = null;

    @Override
    public String identifier() {
        return id;
    }

    @Override
    public void mergeAPIAttributes(Lesson apiVersion) {
        if (!this.id.equals(apiVersion.id))
            throw new IllegalArgumentException("The apiVersion.id must match the current id");

        this.description = apiVersion.description;
        this.postedDate = apiVersion.postedDate;
        this.transcriptSource = apiVersion.transcriptSource;
        this.lessonNumber = apiVersion.lessonNumber;
        this.teacherAidSource = apiVersion.teacherAidSource;
        this.videoSource = apiVersion.videoSource;
        this.title = apiVersion.title;
        this.audioSource = apiVersion.audioSource;
        this.audioLength = apiVersion.audioLength;
        this.studentAidSource = apiVersion.studentAidSource;
        this.topics = apiVersion.topics;
        this.studyId = apiVersion.studyId;
    }
}
