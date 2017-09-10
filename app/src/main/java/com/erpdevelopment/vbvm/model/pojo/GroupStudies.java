package com.erpdevelopment.vbvm.model.pojo;

import com.erpdevelopment.vbvm.model.Channel;
import com.erpdevelopment.vbvm.model.GroupStudy;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
