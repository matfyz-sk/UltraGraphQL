package org.hypergraphql.mutation;

import graphql.language.BooleanValue;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import org.apache.jena.vocabulary.XSD;
import org.hypergraphql.mutation.values.DateTimeValue;
import org.hypergraphql.mutation.values.DecimalValue;

import static org.hypergraphql.config.schema.HGQLVocabulary.*;

public final class SPARQLTypeConverter {

    private static final String XSD_PREFIX = XSD.NS;
    private static final String TYPE_PREFIX = "^^";

    private SPARQLTypeConverter() {

    }

    public static String getIRIType(String value) {
        return "<" + value + ">";
    }

    /* This is a proper way how to put triple into database, however Virtuoso has probably some kind of issue making it impossible to use types, therefore just return everything as a string - check the behaviour against old backend. */
    public static String getSchemaScalarType(String value, Class<?> type) {
        if (type == StringValue.class) {
            return formatToSchema(XML_STRING, value);
        } else if (type == IntValue.class) {
            return formatToSchema(XML_INTEGER, value);
        } else if (type == BooleanValue.class) {
            return formatToSchema(XML_BOOLEAN, value);
        } else if (type == DateTimeValue.class) {
            return formatToSchema(XML_DATETIME, value);
        } else if (type == DecimalValue.class || type == FloatValue.class) { /* Float is also formatted as decimal because GraphQL consider Decimals also as Floats, but we want it have as Decimals. */
            return formatToSchema(XML_DECIMAL, value);
        }
        return value;
    }

    private static String formatToSchema(String type, String value) {
        return "\"" + value + "\"" + TYPE_PREFIX + "<" + type + ">";
    }

}
