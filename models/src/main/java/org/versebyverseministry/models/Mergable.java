package org.versebyverseministry.models;

/**
 * Created by thomascarey on 24/06/17.
 */

public interface Mergable<T> {

    String identifier();

    void mergeAPIAttributes(T apiVersion);
}
