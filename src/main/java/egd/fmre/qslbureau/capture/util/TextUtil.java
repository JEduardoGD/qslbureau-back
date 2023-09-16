package egd.fmre.qslbureau.capture.util;

import org.apache.commons.lang3.StringUtils;

public abstract class TextUtil {
    public static String sanitize(String s) {
        if (s == null) {
            return null;
        }
        s = s.trim();
        s = StringUtils.stripAccents(s);
        return s.toUpperCase();
    }
}
