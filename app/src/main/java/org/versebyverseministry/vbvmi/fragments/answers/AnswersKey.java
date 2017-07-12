package org.versebyverseministry.vbvmi.fragments.answers;

import com.google.auto.value.AutoValue;

import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.application.Key;
import org.versebyverseministry.vbvmi.application.MainActivity;

/**
 * Created by thomascarey on 12/07/17.
 */

@AutoValue
public abstract class AnswersKey extends Key {

    @Override
    public int layout() {
        return R.layout.key_answers;
    }

    public static AnswersKey create() {
        return new AutoValue_AnswersKey();
    }

    @Override
    public String stackIdentifier() {
        return MainActivity.StackType.ANSWERS.name();
    }
}
