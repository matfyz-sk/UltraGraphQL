package org.hypergraphql.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class DateUtils {

    public static boolean isValidPatternDate(String dateTimeString) {
        boolean valid = true;
        try {
            DateTime dateTime = new DateTime(dateTimeString);
        } catch (Exception e) {
            valid = false;
        }
        return valid;
    }

    public static boolean isNumericDate(String dateTimeString) {
        try {
            Double.parseDouble(dateTimeString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isObjectDate(Object input) {
        return input instanceof DateTime || input instanceof Date || isLocalDate(input);
    }

    private static boolean isLocalDate(Object input) {
        return input instanceof LocalDate || input instanceof LocalDateTime || input instanceof org.joda.time.LocalDate || input instanceof org.joda.time.LocalDateTime;
    }

    public static DateTime convertMilisToDateTime(String dateTimeString) {
        return new DateTime((Long.parseLong(dateTimeString)), DateTimeZone.UTC);
    }

}
