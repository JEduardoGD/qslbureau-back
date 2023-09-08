package egd.fmre.qslbureau.capture.util;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public abstract class DateTimeUtil {
    public static Date getDateTime() {
        Instant now = Instant.now();
        return Date.from(now);
    }

    public static Calendar getNowCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDateTime());
        return cal;
    }
}
