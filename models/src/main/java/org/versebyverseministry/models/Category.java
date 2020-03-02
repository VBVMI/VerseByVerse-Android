package org.versebyverseministry.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by thomascarey on 2/07/17.
 */

@Table(database = AppDatabase.class)
@Parcel(analyze = {Category.class})
public class Category extends BaseModel implements Mergable<Category> {

    @Column
    @PrimaryKey
    @SerializedName("id")
    @Expose
    public Integer id;

    @Column
    @SerializedName("name")
    @Expose
    public String name;

    @Column
    @SerializedName("order")
    @Expose
    public Integer order;

    @Override
    public String identifier() {
        return id.toString();
    }

    @Override
    public void mergeAPIAttributes(Category apiVersion) {
        if (!this.id.equals(apiVersion.id))
            throw new IllegalArgumentException("The apiVersion.id must match the current id");

        this.name = apiVersion.name;
        this.order = apiVersion.order;
    }

    public static String updated() {
        return "UPDATED-Category";
    }

    public static List<Category> allCategories() {
        return SQLite.select().from(Category.class).orderBy(Category_Table.order, true).queryList();
    }

    public static long countOf() {
        return SQLite.selectCountOf().from(Category.class).count();
    }
}
