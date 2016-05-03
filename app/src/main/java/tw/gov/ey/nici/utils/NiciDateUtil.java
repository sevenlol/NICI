package tw.gov.ey.nici.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NiciDateUtil {
    private static final String POST_DATE_FORMAT = "yyyy-MM-dd";
    private static final String MEETING_DATE_YEAR_SEPARATOR = "年";
    private static final String MEETING_DATE_MONTH_SEPARATOR = "月";
    private static final String MEETING_DATE_DAY_SEPARATOR = "日";

    public static Date parsePostDateStr(String postDateStr) {
        if (postDateStr == null || postDateStr.equals("")) {
            return null;
        }
        try {
            DateFormat df = new SimpleDateFormat(POST_DATE_FORMAT);
            return df.parse(postDateStr);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date parseMeetingDateStr(String meetingDateStr) {
        if (meetingDateStr == null || meetingDateStr.equals("")) {
            return null;
        }

        Date date = parsePostDateStr(meetingDateStr);
        if (date != null) {
            return date;
        }

        if (!checkMeetingDateStr(meetingDateStr)) {
            return null;
        }

        return getMeetingDateStr(meetingDateStr);
    }

    private static Date getMeetingDateStr(String meetingDateStr) {
        Integer year = getInt(meetingDateStr.substring(
                0, meetingDateStr.indexOf(MEETING_DATE_YEAR_SEPARATOR)));
        if (year == null || year < 0) {
            return null;
        }
        // considered as R.O.C. year
        if (year < 1000) {
            year += 1911;
        }
        Integer month = getInt(meetingDateStr.substring(
                meetingDateStr.indexOf(MEETING_DATE_YEAR_SEPARATOR) + 1,
                meetingDateStr.indexOf(MEETING_DATE_MONTH_SEPARATOR)));
        if (month == null || month < 1 || month > 12) {
            return null;
        }
        Integer day = getInt(meetingDateStr.substring(
                meetingDateStr.indexOf(MEETING_DATE_MONTH_SEPARATOR) + 1,
                meetingDateStr.indexOf(MEETING_DATE_DAY_SEPARATOR)));
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, day, 0, 0);
            return calendar.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    private static Integer getInt(String str) {
        if (str == null || str.equals("")) {
            return null;
        }
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static boolean checkMeetingDateStr(String meetingDateStr) {
        return meetingDateStr.contains(MEETING_DATE_YEAR_SEPARATOR) &&
                meetingDateStr.contains(MEETING_DATE_MONTH_SEPARATOR) &&
                meetingDateStr.contains(MEETING_DATE_DAY_SEPARATOR);
    }
}
