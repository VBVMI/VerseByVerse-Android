package org.versebyverseministry.vbvmi.fragments.shared;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.Unbinder;

/**
 * Created by thomascarey on 12/07/17.
 */

public abstract class AbstractFragment extends Fragment {

    protected Unbinder unbinder;
    private Bitmap b = null;

    public boolean shouldBitmapUI() {
        return false;
    }

    @Override public void onDestroyView() {
        if (b != null) {
            BitmapDrawable bd = new BitmapDrawable(b);
            getView().setBackgroundDrawable(bd);
            b = null;
        }

        super.onDestroyView();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(),
                v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getWidth(),
                v.getHeight());
        v.draw(c);
        return b;
    }

    @Override
    public void onResume() {
        if (b != null && !b.isRecycled()) {
            b.recycle();
            b = null;
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if(shouldBitmapUI()) {
            b = loadBitmapFromView(getView());
        }
        super.onPause();
    }

}
