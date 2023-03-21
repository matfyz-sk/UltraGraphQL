package org.hypergraphql.mutation;

import graphql.language.*;
import org.apache.jena.vocabulary.XSD;

public final class SPARQLTypeConvertor {

    private static final String XSD_PREFIX = XSD.NS;
    private static final String TYPE_PREFIX = "^^";

    private SPARQLTypeConvertor() {

    }

    public static String getSchemaScalarType(String value, Class<?> type) {
        if (type == StringValue.class) {
            return formatToSchema("string", value);
        } else if (type == IntValue.class) {
            return formatToSchema("integer", value);
        } else if (type == BooleanValue.class) {
            return formatToSchema("boolean", value);
        } else if (type == FloatValue.class) {
            return formatToSchema("float", value);
        } else if (type == DateTimeValue.class) {
            return formatToSchema("dateTime", value);
        }
        return value;
    }

    private static String formatToSchema(String type, String value) {
        return "\"" + value + "\"" + TYPE_PREFIX + "<" + XSD_PREFIX + type + ">";
    }

}
