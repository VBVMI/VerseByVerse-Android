package com.erpdevelopment.vbvm;

import android.text.Html;
import android.text.Spanned;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by thomascarey on 27/08/17.
 */

public class StringHelpers {

    public static Spanned fromHtmlString(String str) {
        return Html.fromHtml(str);
    }

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    public static String changeStringCase(String s) {

        final String DELIMITERS = " '-/";

        StringBuilder sb = new StringBuilder();
        boolean capNext = true;

        for (char c : s.toCharArray()) {
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : Character.toLowerCase(c);
            sb.append(c);
            capNext = (DELIMITERS.indexOf((int) c) >= 0);
        }
        return sb.toString();
    }
}
