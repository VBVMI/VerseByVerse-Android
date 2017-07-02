package org.versebyverseministry.vbvmi.api;

/**
 * Created by thomascarey on 2/07/17.
 */

public interface MergeOperation<T> {
    public abstract void didPersist(T instance);
    public abstract void didDelete(T instance);
}
