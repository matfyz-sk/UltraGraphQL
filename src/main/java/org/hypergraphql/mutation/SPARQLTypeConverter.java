package org.hypergraphql.mutation;

import graphql.language.BooleanValue;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import org.apache.jena.vocabulary.XSD;
import org.hypergraphql.mutation.values.DateTimeValue;
import org.hypergraphql.mutation.values.DecimalValue;

public final class SPARQLTypeConverter {

    private static final String XSD_PREFIX = XSD.NS;
    private static final String TYPE_PREFIX = "^^";

    private SPARQLTypeConverter() {

    }

    public static String getSchemaScalarType(String value, Class<?> type) {
        if (type == StringValue.class) {
            return formatToSchema("string", value);
        } else if (type == IntValue.class) {
            return formatToSchema("integer", value);
        } else if (type == BooleanValue.class) {
            return formatToSchema("boolean", value);
        } else if (type == DateTimeValue.class) {
            return formatToSchema("dateTime", value);
        } else if (type == DecimalValue.class || type == FloatValue.class) { /* Float is also formatted as decimal because GraphQL consider Decimals also as Floats, but we want it have as Decimals. */
            return formatToSchema("decimal", value);
        }
        return value;
    }

    private static String formatToSchema(String type, String value) {
        return "\"" + value + "\"" + TYPE_PREFIX + "<" + XSD_PREFIX + type + ">";
    }

}
