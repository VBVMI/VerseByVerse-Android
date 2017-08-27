package org.versebyverseministry.vbvmi;

import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;

/**
 * Created by thomascarey on 27/08/17.
 */

public class StringHelpers {

    public static Spanned fromHtmlString(String str) {
        return Html.fromHtml(str);
    }
}
