package de.lordstave.sqlbansystem.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static long parseDateDiff(String time) {
        Pattern pattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", 2);
        Matcher matcher = pattern.matcher(time);
        long years = 0;
        long month = 0;
        long weeks = 0;
        long days = 0;
        long hours = 0;
        long minutes = 0;
        long seconds = 0;
        boolean found = false;
        while (matcher.find()) {
            if (matcher.group() == null || matcher.group().isEmpty()) continue;
            int i = 0;
            while (i < matcher.groupCount()) {
                if (matcher.group(i) != null && !matcher.group(i).isEmpty()) {
                    found = true;
                    break;
                }
                ++i;
            }
            if (!found) continue;
            if (matcher.group(1) != null && !matcher.group(1).isEmpty()) {
                years = Long.parseLong(matcher.group(1));
            }
            if (matcher.group(2) != null && !matcher.group(2).isEmpty()) {
                month = Long.parseLong(matcher.group(2));
            }
            if (matcher.group(3) != null && !matcher.group(3).isEmpty()) {
                weeks = Long.parseLong(matcher.group(3));
            }
            if (matcher.group(4) != null && !matcher.group(4).isEmpty()) {
                days = Long.parseLong(matcher.group(4));
            }
            if (matcher.group(5) != null && !matcher.group(5).isEmpty()) {
                hours = Long.parseLong(matcher.group(5));
            }
            if (matcher.group(6) != null && !matcher.group(6).isEmpty()) {
                minutes = Long.parseLong(matcher.group(6));
            }
            if (matcher.group(7) == null || matcher.group(7).isEmpty()) continue;
            seconds = Long.parseLong(matcher.group(7));
        }
        if (!found) {
            return -1;
        }
        long millis = 0;
        if (years > 0) {
            millis += years * 224985600000L;
        }
        if (month > 0) {
            millis += month * 18748800000L;
        }
        if (weeks > 0) {
            millis += weeks * 604800000;
        }
        if (days > 0) {
            millis += days * 86400000;
        }
        if (hours > 0) {
            millis += hours * 3600000;
        }
        if (minutes > 0) {
            millis += minutes * 60000;
        }
        if (seconds > 0) {
            millis += seconds * 1000;
        }
        return millis;
    }
}
