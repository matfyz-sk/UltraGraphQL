package org.hypergraphql.schemaextraction;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.*;
import org.joda.time.DateTime;
import sun.util.calendar.BaseCalendar;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class ExtendedScalars {

    private static String typeName(Object input) {
        if (input == null) {
            return "null";
        }
        return input.getClass().getSimpleName();
    }

    public static boolean isDate(Object input) {
        return input instanceof DateTime || input instanceof Date || input instanceof BaseCalendar.Date || isLocalDate(input);
    }

    private static boolean isLocalDate(Object input) {
        return input instanceof LocalDate || input instanceof LocalDateTime || input instanceof org.joda.time.LocalDate || input instanceof org.joda.time.LocalDateTime;
    }

    /**
     * This represents the "DateTime" type which is a representation of org.joda.time.DateTime
     */
    public static final GraphQLScalarType GraphQLDateTime = new GraphQLScalarType("DateTime", "Built-in org.joda.time.DateTime", new Coercing<DateTime, DateTime>() {




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

        @Override
        public DateTime parseLiteral(Object input) {
            if (input instanceof StringValue) {
                try {
                    return new DateTime(((StringValue) input).getValue());
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
    });


}