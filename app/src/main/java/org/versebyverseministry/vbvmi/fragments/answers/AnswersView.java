package org.versebyverseministry.vbvmi.fragments.answers;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;

/**
 * Created by thomascarey on 12/07/17.
 */

public class AnswersView extends RelativeLayout {


    public AnswersView(Context context) {
        super(context);
    }

    public AnswersView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnswersView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public AnswersView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context) {
        if(!isInEditMode()) {
            answersKey = Backstack.getKey(context);
        }
    }

    AnswersKey answersKey;
}
