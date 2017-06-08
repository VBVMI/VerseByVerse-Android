package org.versebyverseministry.vbvmi.api;

/**
 * Created by thomascarey on 8/06/17.
 */

public class APIManager {
    private static final APIManager ourInstance = new APIManager();

    public static APIManager getInstance() {
        return ourInstance;
    }

    private APIManager() {
    }
}
