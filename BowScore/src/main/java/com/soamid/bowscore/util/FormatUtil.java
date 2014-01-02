package com.soamid.bowscore.util;

import java.util.List;

public class FormatUtil {

    public static String formatResult(List<String> results) {
        StringBuilder sb = new StringBuilder();

        for (String result : results) {
            sb.append(result + " ");
        }

        sb.trimToSize();
        return sb.toString();
    }
}