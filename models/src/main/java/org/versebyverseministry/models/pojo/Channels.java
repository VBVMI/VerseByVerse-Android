package org.versebyverseministry.models.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.versebyverseministry.models.Channel;

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
