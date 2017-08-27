package com.erpdevelopment.vbvm;

import android.text.Html;
import android.text.Spanned;

/**
 * Created by thomascarey on 27/08/17.
 */

public class StringHelpers {

    public static Spanned fromHtmlString(String str) {
        return Html.fromHtml(str);
    }
}
