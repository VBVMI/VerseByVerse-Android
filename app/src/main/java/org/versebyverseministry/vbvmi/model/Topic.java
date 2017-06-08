package org.versebyverseministry.vbvmi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;
import org.versebyverseministry.vbvmi.database.AppDatabase;

/**
 * Created by thomascarey on 8/06/17.
 */

@Table(database = AppDatabase.class)
@Parcel(analyze = {Topic.class})
public class Topic extends BaseModel {

    @PrimaryKey
    @SerializedName("ID")
    @Expose
    public String id;

    @Column
    @SerializedName("topic")
    @Expose
    public String topic;

}
