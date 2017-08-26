package org.versebyverseministry.vbvmi.views;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import org.versebyverseministry.vbvmi.R;

import butterknife.ButterKnife;

/**
 * Created by thomascarey on 26/08/17.
 */

public class LoadingView extends ConstraintLayout {

    private ProgressBar progressBar;

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_loading, this, true);

        progressBar = ButterKnife.findById(view, R.id.loading_progress);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }
}
