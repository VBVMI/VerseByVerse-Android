package org.versebyverseministry.models.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.versebyverseministry.models.GroupStudy;

import java.util.List;

/**
 * Created by thomascarey on 9/09/17.
 */

public class GroupStudies {

    @SerializedName("channels")
    @Expose
    private List<GroupStudy> channels = null;

    public List<GroupStudy> getChannels() { return channels; }

    public void setChannels(List<GroupStudy> channels) {
        this.channels = channels;
    }

}
