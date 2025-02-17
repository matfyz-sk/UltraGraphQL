package org.hypergraphql.query.converters;

import org.hypergraphql.config.schema.QueryFieldConfig;
import org.hypergraphql.datamodel.HGQLSchema;
import org.hypergraphql.query.pattern.Query;
import org.hypergraphql.query.pattern.QueryPattern;
import org.hypergraphql.query.pattern.SubQueriesPattern;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hypergraphql.config.schema.HGQLVocabulary.HGQL_QUERY_GET_FIELD;
import static org.hypergraphql.util.GlobalValues._ID;
import static org.hypergraphql.util.GlobalValues._TYPE;

public class HGraphQLConverter {
    private final HGQLSchema schema;

    public HGraphQLConverter(HGQLSchema schema) {

        this.schema = schema;
    }

    private String urisArgSTR(Set<String> uris) {

        final String QUOTE = "\"%s\"";
        final String ARG = "(uris:[%s])";

        Set<String> quotedUris = new HashSet<>();

        for (String uri : uris) {
            quotedUris.add(String.format(QUOTE, uri));
        }

        String uriSequence = String.join(",", quotedUris);

        return String.format(ARG, uriSequence);
    }

    private String getArgsSTR(Map<String, Object> getArgs) {

        if (getArgs != null) {
            return "";
        }
        final String ARG = "(%s)";

        String argsStr = "";
        return String.format(ARG, argsStr);
    }

    private String langSTR(Map<String, Object> langArg) {

        if (langArg == null) {
            return "";
        }
        if (langArg.containsKey(SPARQLServiceConverter.LANG)) {
            return "(lang:\"" + langArg.get(SPARQLServiceConverter.LANG) + "\")";
        }
        return "";
    }

    private String querySTR(String content) {

        final String QUERY = "{ %s }";
        return String.format(QUERY, content);
    }


    public String convertToHGraphQL(Query jsonQuery, Set<String> input, String rootType) {

        Map<String, QueryFieldConfig> queryFields = schema.getQueryFields();
        boolean root = (!jsonQuery.isSubQuery() && queryFields.containsKey(((QueryPattern) jsonQuery).name));

        if (root) {
            if (queryFields.get(((QueryPattern) jsonQuery).name).type().equals(HGQL_QUERY_GET_FIELD)) {
                return getSelectRoot_GET((QueryPattern) jsonQuery);
            } else {
                return getSelectRoot_GET_BY_ID((QueryPattern) jsonQuery);
            }
        } else {
            return getSelectNonRoot((SubQueriesPattern) jsonQuery, input, rootType);
        }
    }

    private String getSelectRoot_GET_BY_ID(QueryPattern jsonQuery) {

        Set<String> uris = (Set<String>) jsonQuery.args.get(SPARQLServiceConverter.LANG);
        String key = jsonQuery.name + urisArgSTR(uris);
        String content = getSubQuery(jsonQuery.fields, jsonQuery.targetType);
        return querySTR(key + content);
    }


    private String getSelectRoot_GET(QueryPattern jsonQuery) {

        String key = jsonQuery.name + getArgsSTR(jsonQuery.args);
        String content = getSubQuery(jsonQuery.fields, jsonQuery.targetType);
        return querySTR(key + content);
    }

    private String getSelectNonRoot(SubQueriesPattern jsonQuery, Set<String> input, String rootType) {

        String topQueryFieldName = rootType + "_GET_BY_ID";
        String key = topQueryFieldName + urisArgSTR(input);
        String content = getSubQuery(jsonQuery, rootType);
        return querySTR(key + content);
    }

    private String getSubQuery(SubQueriesPattern fieldsJson, String parentType) {

        Set<String> subQueryStrings = new HashSet<>();

        if (schema.getTypes().containsKey(parentType)) {
            subQueryStrings.add(_ID);
            subQueryStrings.add(_TYPE);
        }

        if (fieldsJson == null || fieldsJson.subqueries == null) {
            if (subQueryStrings.isEmpty()) {
                return "";
            } else {
                return querySTR(String.join(" ", subQueryStrings));
            }
        } else {


            for (QueryPattern field : fieldsJson.subqueries) {
                SubQueriesPattern fieldsArray = field.fields;
                String arg = langSTR(field.args);
                String fieldString = field.name + arg + " " + getSubQuery(fieldsArray, field.targetType);
                subQueryStrings.add(fieldString);
            }
        }

        if (!subQueryStrings.isEmpty()) {
            return querySTR(String.join(" ", subQueryStrings));
        } else {
            return "";
        }
    }

}
