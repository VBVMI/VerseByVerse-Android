package org.versebyverseministry.models.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.versebyverseministry.models.Lesson;

import java.util.List;

/**
 * Created by thomascarey on 17/07/17.
 */

public class Lessons {
    @SerializedName("lessons")
    @Expose
    private List<Lesson> lessons = null;

    public List<Lesson> getLessons() { return lessons; }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }
}
