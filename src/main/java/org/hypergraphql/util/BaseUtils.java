package org.hypergraphql.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class BaseUtils {

    private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mmZZ";

    private BaseUtils() {

    }

    public static String createObjectKeyWithoutKnownPrefixes(String baseKey) {
        if (baseKey == null || baseKey.isEmpty()) {
            return baseKey;
        }
        return baseKey.replace(GlobalValues.COURSES_ONTOLOGY_UGQL_PREFIX_WITH_UNDERSCORE, GlobalValues.EMPTY_STRING).replace(GlobalValues.COURSES_DATA_UGQL_PREFIX_WITH_UNDERSCORE, GlobalValues.EMPTY_STRING);
    }

    /**
     * In case a model class should start with lowerCase then use this method.
     */
    public static String createObjectKeyWithoutKnownPrefixesFirstLowerCase(String baseKey) {
        if (baseKey == null || baseKey.isEmpty()) {
            return baseKey;
        }
        char[] keyWithoutPrefix = baseKey.replace(GlobalValues.COURSES_ONTOLOGY_UGQL_PREFIX_WITH_UNDERSCORE, GlobalValues.EMPTY_STRING).replace(GlobalValues.COURSES_DATA_UGQL_PREFIX_WITH_UNDERSCORE, GlobalValues.EMPTY_STRING).toCharArray();
        keyWithoutPrefix[0] = Character.toLowerCase(keyWithoutPrefix[0]);
        return new String(keyWithoutPrefix);
    }

    public static boolean isValidPatternDate(String dateTimeString) {
        boolean valid = true;
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern(DATETIME_PATTERN);
            DateTime dob = formatter.parseDateTime(dateTimeString);
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
        return new DateTime((Long.parseLong(dateTimeString) * 1000L), DateTimeZone.UTC);
    }

}
