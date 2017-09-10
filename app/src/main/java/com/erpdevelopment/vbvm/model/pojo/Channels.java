package com.erpdevelopment.vbvm.model.pojo;

import com.erpdevelopment.vbvm.model.Channel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by thomascarey on 9/09/17.
 */

public class Channels {

    @SerializedName("channels")
    @Expose
    private List<Channel> channels = null;

    public List<Channel> getChannels() { return channels; }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

}
