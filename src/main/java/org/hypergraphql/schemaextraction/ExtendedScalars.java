package org.hypergraphql.schemaextraction;

import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.schema.*;
import org.joda.time.DateTime;

import java.time.DateTimeException;

import static org.hypergraphql.util.DateUtils.*;

public class ExtendedScalars {

    private static String typeName(Object input) {
        if (input == null) {
            return "null";
        }
        return input.getClass().getSimpleName();
    }

    /**
     * This represents the "DateTime" type which is a representation of org.joda.time.DateTime
     */
    public static final GraphQLScalarType GraphQLDateTime = GraphQLScalarType.newScalar().name("DateTime").description("Built-in simplified org.joda.time.DateTime. Returns millis and string representation in ISO format. The format should be 2010-06-30T01:20+02:00 or DateTime in milliseconds.").coercing(new Coercing<DateTime, DateTime>() {

        private DateTime convertImpl(Object input) {
            if (isObjectDate(input)) {
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
                    String dateTimeString = ((StringValue) input).getValue();
                    boolean isNumericDate = isNumericDate(dateTimeString);

                    if (!isValidPatternDate(dateTimeString) && !isNumericDate) {
                        throw new DateTimeException(
                                "Wrong DateTime format for input:" + input + ". Please use the format 2010-06-30T01:20+02:00 or DateTime in milliseconds."
                        );
                    }
                    return isNumericDate ? convertMilisToDateTime(dateTimeString) : new DateTime(dateTimeString);
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