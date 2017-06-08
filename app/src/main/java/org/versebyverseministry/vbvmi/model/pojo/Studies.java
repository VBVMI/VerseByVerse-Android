package org.versebyverseministry.vbvmi.model.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.versebyverseministry.vbvmi.model.Study;

import java.util.List;

/**
 * Created by thomascarey on 8/06/17.
 */

public class Studies {

    @SerializedName("studies")
    @Expose
    private List<Study> studies = null;

    public List<Study> getStudies() {
        return studies;
    }

    public void setStudies(List<Study> studies) {
        this.studies = studies;
    }

}
