package org.versebyverseministry.models;

import androidx.annotation.Nullable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by thomascarey on 1/10/17.
 */
@Table(database = AppDatabase.class)
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
