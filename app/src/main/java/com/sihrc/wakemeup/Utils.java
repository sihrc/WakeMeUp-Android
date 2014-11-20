package com.sihrc.wakemeup;

/**
 * Created by sihrc on 11/20/14.
 */
public class Utils {
    public static String formatTimeAmPm(int hour, int minute) {
        String time = " AM";
        if (hour >= 12) {
            hour -= 12;
            time = " PM";
            if (hour == 0) {
                hour = 12;
                return String.valueOf(hour) + " : " + String.valueOf(minute) + time;
            }
        }

        return ' ' + String.valueOf(hour) + " : " + String.valueOf(minute) + time;
    }
}
