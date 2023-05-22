package org.hypergraphql.schemaextraction;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class ExtendedScalars {

    private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mmZZ";

    private static String typeName(Object input) {
        if (input == null) {
            return "null";
        }
        return input.getClass().getSimpleName();
    }

    public static boolean isDate(Object input) {
        return input instanceof DateTime || input instanceof Date || isLocalDate(input);
    }

    private static boolean isLocalDate(Object input) {
        return input instanceof LocalDate || input instanceof LocalDateTime || input instanceof org.joda.time.LocalDate || input instanceof org.joda.time.LocalDateTime;
    }

    /**
     * This represents the "DateTime" type which is a representation of org.joda.time.DateTime
     */
    public static final GraphQLScalarType GraphQLDateTime = GraphQLScalarType.newScalar().name("DateTime").description("Built-in simplified org.joda.time.DateTime. Returns millis and string representation in ISO format. The format should be 2010-06-30T01:20+02:00 or DateTime in milliseconds.").coercing(new Coercing<DateTime, DateTime>() {

        private DateTime convertImpl(Object input) {
            if (isDate(input)) {
                try {
                    return new DateTime(input.toString());
                } catch (DateTimeException e) {
                    return null;
                }
            }
            return null;

        }

        @Override
        public DateTime serialize(Object input) {
            DateTime result = convertImpl(input);
            if (result == null) {
                throw new CoercingSerializeException(
                        "Expected type 'DateTime' but was '" + typeName(input) + "'."
                );
            }
            return result;
        }

        @Override
        public DateTime parseValue(Object input) {
            DateTime result = convertImpl(input);
            if (result == null) {
                throw new CoercingParseValueException(
                        "Expected type 'DateTime' but was '" + typeName(input) + "'."
                );
            }
            return result;
        }

        private boolean isValidDate(String dateTimeString) {
            boolean valid = true;
            try {
                DateTimeFormatter formatter = DateTimeFormat.forPattern(DATETIME_PATTERN);
                DateTime dob = formatter.parseDateTime(dateTimeString);
            } catch (Exception e) {
                valid = false;
            }
            return valid;
        }

        public static boolean isNumeric(String dateTimeString) {
            try {
                Double.parseDouble(dateTimeString);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public DateTime parseLiteral(Object input) {
            if (input instanceof StringValue) {
                try {
                    String dateTimeString = ((StringValue) input).getValue();

                    boolean isNumericDate = isNumeric(dateTimeString);

                    if (!isValidDate(dateTimeString) && !isNumericDate) {
                        throw new DateTimeException(
                                "Wrong DateTime format for input:" + input + ". Please use the format 2010-06-30T01:20+02:00 or DateTime in milliseconds."
                        );
                    }
                    return isNumericDate ? new DateTime((Long.parseLong(dateTimeString) * 1000L), DateTimeZone.UTC) : new DateTime(dateTimeString);
                } catch (NumberFormatException e) {
                    throw new DateTimeException(
                            "Unable to turn AST input into a 'DateTime' : '" + input + "'"
                    );
                }
            } else if (input instanceof IntValue) {
                return new DateTime(((IntValue) input).getValue());
            }
            throw new CoercingParseLiteralException(
                    "Expected AST type 'IntValue', 'StringValue','Date', 'BaseCalendar.Date', but was '" + typeName(input) + "'."
            );
        }
    }).build();
}