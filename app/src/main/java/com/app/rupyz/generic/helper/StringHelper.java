package com.app.rupyz.generic.helper;

import java.util.Locale;

public class StringHelper {

    public static String toCamelCase(final String init) {
        if (init == null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(Character.toUpperCase(word.charAt(0)));
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length() == init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }

    public static String getPrefix(String f_name) {
        return f_name.substring(0, 1).toUpperCase();
    }

    public static String printName(String name) {
        String splitName = "";
        if (name == null)
            return splitName;

        if (name.toLowerCase(Locale.ROOT).contains("m/s.")) {
            splitName = name.substring(4);
        } else if (name.toLowerCase(Locale.ROOT).contains("m/s")) {
            splitName = name.substring(3);
        } else {
            splitName = name;
        }

        return splitName;
    }
}
