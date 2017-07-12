package org.versebyverseministry.vbvmi.fragments.studies;

import com.google.auto.value.AutoValue;

import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.application.Key;
import org.versebyverseministry.vbvmi.application.MainActivity;

/**
 * Created by thomascarey on 12/07/17.
 */

@AutoValue
public abstract class StudiesKey extends Key {

    @Override
    public int layout() {
        return R.layout.key_studies;
    }

    public static StudiesKey create() {
        return new AutoValue_StudiesKey();
    }

    @Override
    public String stackIdentifier() {
        return MainActivity.StackType.STUDIES.name();
    }
}
