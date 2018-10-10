package com.erpdevelopment.vbvm.model;

import android.support.annotation.Nullable;

import com.erpdevelopment.vbvm.database.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

/**
 * Created by thomascarey on 1/10/17.
 */
@Table(database = AppDatabase.class)
@Parcel(analyze = {MetaData.class})
public class MetaData extends BaseModel {

    @PrimaryKey
    public String id;

    @Column
    @Nullable
    public String currentLessonId;

    public static String updated() {
        return "UPDATED-MetaData";
    }
}
