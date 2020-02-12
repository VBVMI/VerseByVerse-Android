package com.erpdevelopment.vbvm.views;

import android.content.Context;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.erpdevelopment.vbvm.R;

/**
 * Created by thomascarey on 2/09/17.
 */

public class EmptyView extends CoordinatorLayout {

    private TextView textView;

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_empty, this, true);

        if (view != null) {
            textView = view.findViewById(R.id.empty_text_view);
        }

    }
}
