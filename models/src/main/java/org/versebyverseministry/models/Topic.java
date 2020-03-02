package org.versebyverseministry.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;


/**
 * Created by thomascarey on 8/06/17.
 */

@Table(database = AppDatabase.class)
public class Topic extends BaseModel {

    @PrimaryKey
    @SerializedName("ID")
    @Expose
    public String id;

    @Column
    @SerializedName("topic")
    @Expose
    public String topic;

    public static String updated() {
        return "UPDATED-Topic";
    }
}
