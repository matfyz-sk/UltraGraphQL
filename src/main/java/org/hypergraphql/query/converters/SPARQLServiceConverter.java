package org.hypergraphql.query.converters;

import org.apache.commons.lang3.StringUtils;
import org.hypergraphql.config.schema.HGQLVocabulary;
import org.hypergraphql.config.schema.QueryFieldConfig;
import org.hypergraphql.datafetching.services.ManifoldService;
import org.hypergraphql.datafetching.services.SPARQLEndpointService;
import org.hypergraphql.datafetching.services.Service;
import org.hypergraphql.datamodel.HGQLSchema;
import org.hypergraphql.exception.GraphQLIllegalArgumentException;
import org.hypergraphql.query.pattern.Query;
import org.hypergraphql.query.pattern.QueryPattern;
import org.hypergraphql.query.pattern.SubQueriesPattern;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hypergraphql.config.schema.HGQLVocabulary.*;
import static org.hypergraphql.mutation.SPARQLTypeConverter.TYPE_PREFIX;
import static org.hypergraphql.util.GlobalValues.*;

/**
 * The SPARQLServiceConverter provides methods to covert GraphQl queries into SPARQL queries according to the schema this
 * class is instantiated with.
 */
public class SPARQLServiceConverter {

    private static final String RDF_TYPE_URI = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
    private static final String RDFS_SUB_CLASS_OF_WITH_ASTERISK = "<http://www.w3.org/2000/01/rdf-schema#subClassOf>*";
    private static final String NAME = "name";
    private static final String URIS = "uris";
    private static final String NODE_ID = "nodeId";
    private static final String TARGET_NAME = "targetName";
    private static final String PARENT_ID = "parentId";
    private static final String FIELDS = "fields";
    private static final String SAMEAS = "sameas";
    private static final String TYPE_SUFFIX_QUERY_BUILDER = "_t";

    public static final String ARGS = "args";
    public static final String LANG = "lang";
    public static final String ORDER = "order";
    public static final String ORDER_DESC = "DESC";
    public static final String ORDER_ASC = "ASC";

    private final HGQLSchema schema;

    public SPARQLServiceConverter(HGQLSchema schema) {
        this.schema = schema;
    }

    /**
     * Wraps the SPARQL OPTIONAL clause around the given sparqlPattern.
     *
     * @param sparqlPattern SPARQL pattern
     * @return SPARQL OPTIONAL clause
     */
    public static String optionalClause(String sparqlPattern) {
        return " OPTIONAL { " + sparqlPattern + " } ";
    }

    /**
     * Wraps the SPARQL OPTIONAL clause around the given sparqlPattern.
     *
     * @param sparqlPattern SPARQL pattern
     * @return SPARQL OPTIONAL clause
     */
    public static String noOptionalClause(String sparqlPattern) {
        return " { " + sparqlPattern + " } ";
    }

    /**
     * Wraps the SPARQL FILTER NOT EXISTS clause around the given sparqlPattern.
     *
     * @param sparqlPattern SPARQL pattern
     * @return SPARQL OPTIONAL clause
     */
    public static String filterNotExists(String sparqlPattern) {
        return " FILTER NOT EXISTS { " + sparqlPattern + " } ";
    }

    /**
     * Wraps the SPARQL FILTER NOT EXISTS clause around the given sparqlPattern.
     *
     * @param triplesType The triple(s) in EXISTS part
     * @return SPARQL FILTER EXISTS clause
     */
    private String filterExists(String triplesType) {
        if (triplesType.isEmpty()) {
            return "";
        }
        return " FILTER ( EXISTS {" + triplesType + "}) ";
    }

    /**
     * Generates a SPARQL Query where the given id is selected from the values defined by the sparqlPattern.
     * The results may be restricted with limit and offset with the given limitOffset variable.
     *
     * @param id            Variable that is selected in the query
     * @param sparqlPattern SPARQL Pattern defining the data. WHERE section of an SPARQL Query
     * @param limitOffset   Usually the limit or offset of the Query but anything that is valid after the WHERE clause is possible. (Like ORDER BY)
     * @return
     */
    private String selectSubqueryClause(String id, String sparqlPattern, String limitOffset) {
        return "{ SELECT " + toVar(id) + " WHERE { " + sparqlPattern + " } " + limitOffset + " } ";
    }

    /**
     * Generates a SPARQL query in which the given graphID is queried with the given query (where). The query is uses a
     * wildcard in the selection this means all variables are queried.
     *
     * @param where Graph to access
     * @return
     */
    private String selectQueryClause(String where) {
        return "SELECT * WHERE { " + graphClause("", where, null, null) + " } "; //TODO check here
    }

    /**
     * Generates a SPARQL query in which the given graphID is queried with the given query (where). The query is uses a
     * wildcard in the selection this means all variables are queried.
     *
     * @param where   Graph to access
     * @param graphID Query for the graph
     * @return
     */
    private String selectQueryClause(String orderByValues, String filterByValues, String filterById, String where, String graphID) {
        return "SELECT * WHERE { " + graphClause(graphID, where, filterByValues, filterById) + " } " + (orderByValues != null ? orderByValues : "");
    }


    /**
     * Generates a SPARQL GRAPH clause in which the given graphID is queried by the given query (where).
     *
     * @param graphID Graph to access
     * @param where   Query for the graph
     * @return SPARQL GRAPH clause
     */
    private String graphClause(String graphID, String where, String filterByValues, String filterById) {
        if (StringUtils.isEmpty(graphID)) {
            return where;
        } else {
            return "GRAPH <" + graphID + "> { " + (filterById != null ? filterById : "") + where + " } " + (filterByValues != null ? filterByValues : "");
        }
    }

    /**
     * Generates a SPARQL VALUES clause for the given id. The VALUES clause is commonly used to filter out results (id) that do NOT
     * match the given input. The given id is typically a SPARQL variable. Only supports single column input.
     *
     * @param id    URI or SPARQL variable the filter is applied
     * @param input List of values id should match
     * @return Corresponding VALUES clause
     */
    private String valuesClause(String id, Set<String> input) {

        if (id == null || input == null || input.isEmpty()) {
            return null;
        }

        String var = toVar(id);
        Set<String> uris = new HashSet<>();
        input.forEach(uri -> uris.add(uriToResource(uri)));

        String urisConcat = String.join(" ", uris);

        return "VALUES " + var + " { " + urisConcat + " } ";
    }

    private String filterClause(final String id, final Set<String> input) {

        String var = toVar(id);
        Set<String> uris = new HashSet<>();
        input.forEach(uri -> uris.add(uriToResource(uri)));

        String urisConcat = String.join(" , ", uris);

        return "FILTER ( " + var + " IN ( " + urisConcat + " ) )";
    }

    /**
     * Filter clause that cheks if the id (sparql variable) is a literal
     *
     * @param id sparql variable
     * @return Filter clause ensuring that the given id is a literal
     */
    private String isLiteralClause(String id) {
        return "FILTER(isLiteral(" + toVar(id) + "))";
    }

    /**
     * Generates the LIMIT and OFFSET clauses if they are defined in the given field/type (jsonQuery).
     *
     * @param query
     * @return
     */
    private String limitOffsetClause(QueryPattern query) {
        String limit = "";
        String offset = "";
        if (query.args != null) {
            if (query.args.containsKey(LIMIT_ARGUMENT)) {
                limit = limitClause((long) query.args.get(LIMIT_ARGUMENT));
            }
            if (query.args.containsKey(OFFSET_ARGUMENT)) {
                offset = offsetClause((long) query.args.get(OFFSET_ARGUMENT));
            }
        }
        return offset + limit;
    }

    /**
     * Wraps the SPARQL LIMIT clause around the given limit
     *
     * @param limit
     * @return
     */
    private String limitClause(long limit) {
        return "LIMIT " + limit + " ";
    }

    /**
     * Wraps the SPARQL OFFSET clause around the given offset
     *
     * @param offset
     * @return
     */
    private String offsetClause(long offset) {
        return "OFFSET " + offset + " ";
    }


    private String orderClause(QueryPattern query, List<String> orderBy) {
        String order = "";
        if (query.args != null && query.args.containsKey(ORDER)) {
            order = (String) query.args.get(ORDER);

            if (orderBy != null) {
                orderBy.add((order.equals(ORDER_DESC) ? "DESC" : "ASC") + "(" + toVar(query.nodeId) + ")");
                return "";
            }

            if (order.equals(ORDER_DESC)) {
                return "ORDER  BY DESC(" + toVar(query.nodeId) + ")";
            } else if (order.equals(ORDER_ASC)) {
                return "ORDER  BY ASC(" + toVar(query.nodeId) + ")";
            }
        }
        return "";
    }

    private String filterClause(QueryPattern query) {
        Set<Object> objects = getValuesFromArgument(query.args, EQUALS_ARGUMENT);
        return filterLogic(new ArrayList<>(objects), query.targetType, query.nodeId);
    }

    private List<String> getSchemaScalarType(String value, String type) {
        if (SCALAR_STRING.equals(type)) {
            return formatToSchema(XML_STRING, value);
        } else if (SCALAR_INT.equals(type)) {
            return formatToSchema(XML_INTEGER, value);
        } else if (SCALAR_BOOLEAN.equals(type)) {
            return formatToSchema(XML_BOOLEAN, value);
        } else if (SCALAR_DATETIME.equals(type)) {
            return formatToSchema(XML_DATETIME, value);
        } else if (SCALAR_DECIMAL.equals(type) || SCALAR_FLOAT.equals(type)) {
            return formatToSchema(XML_DECIMAL, value);
        }
        return null;
    }

    private List<String> formatToSchema(String type, String value) {
        if (XML_STRING.equals(type)) {
            return Arrays.asList("\"" + value + "\"", formatToSchemaTypeWithXMLType(value, type));
        }
        return Collections.singletonList(formatToSchemaTypeWithXMLType(value, type));
    }

    private String formatToSchemaTypeWithXMLType(String value, String type) {
        return "\"" + value + "\"" + TYPE_PREFIX + "<" + type + ">";
    }

    private String filterLogic(List<Object> objects, String type, String nodeId) {
        if (objects == null || objects.isEmpty() || objects.stream().noneMatch(Objects::nonNull)) {
            return null;
        }

        if (objects.size() == 1) {
            String valueAsString = objects.get(0).toString();
            List<String> formattedValueTypes = getSchemaScalarType(valueAsString, type);
            return toVar(nodeId) + " IN (" + getFormattedSingleSchemaTypeWithValue(formattedValueTypes, objects.get(0)) + ")";
        }
        return toVar(nodeId) + " IN (" + objects.stream().map(value -> {
            String valueAsString = value.toString();
            List<String> formattedValueTypes = getSchemaScalarType(valueAsString, type);
            return getFormattedSingleSchemaTypeWithValue(formattedValueTypes, value);
        }).collect(Collectors.joining(", ")) + ")";
    }

    private String getFormattedSingleSchemaTypeWithValue(List<String> formattedValueTypes, Object objectValue) {
        if (formattedValueTypes == null || formattedValueTypes.isEmpty()) {
            return getObjectAsString(objectValue);
        }
        return String.join(", ", formattedValueTypes);
    }

    private String getObjectAsString(Object object) {
        return "\"" + object + "\"";

        /* This is a proper way how to put triple into database, however Virtuoso has probably some kind of issue making it impossible to use types, therefore just return everything as a string. */
        /*if (object instanceof String) {
            return "\"" + object + "\"";
        }

        if(object instanceof Boolean) {
            return String.valueOf(object);
        }*/
    }

    /**
     * Format the given URI to a URI in SPARQL syntax
     *
     * @param uri
     * @return
     */
    public static String uriToResource(String uri) {
        return "<" + uri + ">";
    }

    /**
     * Converts the given id into a SPARQL variable
     *
     * @param id
     * @return SPARQL variable
     */
    public static String toVar(String id) {
        return "?" + id;
    }

    /**
     * Format the given subject predicate and object into a SPARQL sentence.
     *
     * @param subject
     * @param predicate
     * @param object
     * @return SPARQL sentence
     */
    public static String toTriple(String subject, String predicate, String object) {
        return subject + " " + predicate + " " + object + " .";
    }

    /**
     * Returns a SPARQL language filter for the given field if the LANG argument is defined.
     *
     * @param field
     * @return If LANG argument is defined return SPARQL language filter else empty String.
     */
    private String langFilterClause(QueryPattern field) {
        String nodeVar = toVar(field.nodeId);
        return (field.args.containsKey(LANG)) ? "FILTER (lang(" + nodeVar + ") = \"" + field.args.get(LANG) + "\") . " : "";
    }

    /**
     * Returns a SPARQL sentence with parentId and node Id as SPARQL variable and predicateURI as URI. parentId is used
     * as subject and nodeId as object. If typeURI is not an empty string the result is extended by the restriction that
     * nodeId is a rdf:type of typeURI.
     *
     * @param parentId     Id to be used as variable in the sentence subject.
     * @param nodeId       Id to be used as variable in the sentence object.
     * @param predicateURI predicate of the sentence. If the predicate has sameAs properties then the predicateURI is a property path
     * @param typeURI      Required type of the given nodeId,
     * @return
     */
    private String fieldPattern(String parentId, String nodeId, String predicateURI, String typeURI) {
        String predicateTriple = (parentId.isEmpty()) ? "" : toTriple(toVar(parentId), predicateURI, toVar(nodeId));  // parentId == "" : "" | "?parentId <predicateURI> ?nodeId."
        String typeTriple = filterWithSubclassOf(typeURI, nodeId);
        return predicateTriple + typeTriple;
    }

    private String filterWithSubclassOf(String typeURI, String nodeId) {
        if (typeURI == null || typeURI.isEmpty()) {
            return "";
        }
        String nodeIdType = toVar(nodeId) + TYPE_SUFFIX_QUERY_BUILDER;
        String typeTriple = toTriple(toVar(nodeId), RDF_TYPE_URI, nodeIdType);
        String typeSubClassOfTriple = toTriple(nodeIdType, RDFS_SUB_CLASS_OF_WITH_ASTERISK, typeURI);
        return filterExists(typeTriple + " " + typeSubClassOfTriple);
    }

    /**
     * Generates A SPARQL query from a given GraphQl SelectionSet.
     *
     * @param query
     * @param input
     * @param rootType
     * @param serviceId id of the service that called this method. Used to select the right service from a ManifoldService
     * @return
     */
    public String getSelectQuery(Query query, Set<String> input, String rootType, String serviceId) {

        Map<String, QueryFieldConfig> queryFields = schema.getQueryFields();

        boolean root = (!query.isSubQuery() && queryFields.containsKey(((QueryPattern) query).name));

        if (root) {
            return getSelectRoot_GET((QueryPattern) query, serviceId);
        } else {
            return getSelectNonRoot((SubQueriesPattern) query, input, rootType, serviceId);
        }
    }

    /**
     * Generates A SPARQL query from a given GraphQl SelectionSet with no restrictions to the root selection.
     *
     * @param queryField GraphQl SelectionSet
     * @param serviceId  id of the service that called this method. Used to select the right service from a ManifoldService
     * @return
     */
    private String getSelectRoot_GET(QueryPattern queryField, String serviceId) {

        String targetName = queryField.targetType;
        String targetURI = schema.getTypes().get(targetName).getId();
        String graphID = getGraphId(queryField, serviceId);  // The Graph is defined over the HGQL Schema directive service
        String nodeId = queryField.nodeId;   // SPARQL variable
        String limitOffsetSTR = limitOffsetClause(queryField);
        String orderSTR = orderClause(queryField, null);
        String selectTriple;

        if (hasSameAsTypes(targetName)) {
            Set<String> values = getSameAsTypes(targetName);
            values.add(targetName);
            values = values.stream()
                    .map(s -> schema.getTypes().get(s).getId())
                    .collect(Collectors.toSet());
            String var_sameas = SAMEAS + "_" + nodeId;
            String value = valuesClause(var_sameas, values);
            selectTriple = value + toTriple(toVar(nodeId), RDF_TYPE_URI, toVar(var_sameas));
        } else {
            selectTriple = toTriple(toVar(nodeId), RDF_TYPE_URI, uriToResource(targetURI));
        }

        List<String> orderBy = new ArrayList<>();
        List<String> filter = new ArrayList<>();

        String whereClause = getSubQueries(queryField.fields, "", orderBy, filter);
        String rootSubquery = selectSubqueryClause(nodeId, selectTriple, orderSTR + limitOffsetSTR);

        String limitOffsetSTRRoot = limitOffsetClause(queryField); //TODO add limit and offset from ID query

        return selectQueryClause(getOrderBy(orderBy), getFilterByValues(filter), getFilterById(queryField), rootSubquery + whereClause, graphID);
    }

    private String getOrderBy(List<String> orderBy) {
        if (!orderBy.isEmpty()) {
            return "ORDER BY " + String.join(" ", orderBy);
        }
        return null;
    }

    private String getFilterByValues(List<String> filter) {
        if (!filter.isEmpty()) {
            return "FILTER(" + String.join(" && ", filter) + ")";
        }
        return null;
    }

    private String getFilterById(QueryPattern queryField) {
        Map<String, Object> args = queryField.args;
        if (args.containsKey(_ID)) {
            Set<Object> arguments = getValuesFromArgument(args, _ID);
            return valuesClause(queryField.nodeId, objectSetToStringSet(arguments));
        }
        return null;
    }

    private Set<Object> getValuesFromArgument(Map<String, Object> args, String argumentName) {
        Object object = args.get(argumentName);

        if (object == null) {
            return new HashSet<>();
        }

        if (object instanceof String || object instanceof List<?>) {
            return new HashSet<>(object instanceof String ? Collections.singleton((String) args.get(argumentName)) : (List<Object>) args.get(argumentName));
        }
        throw new GraphQLIllegalArgumentException("Invalid query syntax. Argument [" + argumentName + "] should be defined as a list of values.");
    }

    /**
     * Generates a SPARQL query that queries each given field in jsonQuery and restricts the result to the list given in input.
     * This means only results with one of the input values as subject are left in.
     *
     * @param queries   Multiple field elements
     * @param input     Set of values which the result of the query should match in the subject of a triple
     * @param rootType  type from which the graph is used
     * @param serviceId id of the service that called this method. Used to select the right service from a ManifoldService
     * @return
     */
    private String getSelectNonRoot(SubQueriesPattern queries, Set<String> input, String rootType, String serviceId) {

        final ListIterator<QueryPattern> listIterator = queries.subqueries.listIterator();
        QueryPattern firstField = listIterator.next();
        Service service = schema.getTypes().get(rootType).getFields().get(firstField.name).getService();
        if (service instanceof ManifoldService) {
            service = ((ManifoldService) service).getService(serviceId);
        }
        String graphID = ((SPARQLEndpointService) service).getGraph();
        String parentId = firstField.parentId;
        String valueSTR = valuesClause(parentId, input);   // restrict the ?parentId to the values defined in the input list

        List<String> orderBy = new ArrayList<>();
        List<String> filter = new ArrayList<>();

        StringBuilder whereClause = new StringBuilder();
        whereClause.append(getFieldSubquery(firstField, valueSTR, orderBy, filter));   //ToDo: Review this line -> effect on query results //TODO ADD FILTER HERE
        //queries.elements().forEachRemaining(field -> whereClause.append(getFieldSubquery(field)));

        //TODO check if it should be as List
        List<String> filterById = new ArrayList<>();

        while (listIterator.hasNext()) {
            QueryPattern queryPattern = listIterator.next();
            filterById.add(getFilterById(queryPattern));
            whereClause.append(getFieldSubquery(queryPattern, valueSTR, orderBy, filter));
        }

        String orderByValues = getOrderBy(orderBy);
        String filterByValues = getFilterByValues(filter);

        return selectQueryClause(orderByValues, filterByValues, null, valueSTR + (whereClause), graphID);
    }

    private Set<String> objectSetToStringSet(Set<Object> objectSet) {
        return objectSet.stream().map(Object::toString).collect(Collectors.toSet());
    }

    /**
     * Generates a SPARQL query for the given field and also for the subfields of the field.
     *
     * @param field
     * @return
     */
    private String getFieldSubquery(QueryPattern field, String rootValues, List<String> orderBy, List<String> filter) {

        String fieldName = field.name;

        if (HGQLVocabulary.JSONLD.containsKey(fieldName)) {   // Check if the given field is none of the internal fields like _id or _type
            return "";
        }

        String fieldURI = schema.getFields().get(fieldName).getId();
        // if field has sameAs fields replace field uri with property path querying all sameAs fields
        if (hasSameAsFields(fieldName)) {
            Set<String> sameAs_fields = getSameAsFields(fieldName).stream()
                    .map(s -> schema.getFields().get(s).getId())
                    .collect(Collectors.toSet());
            sameAs_fields.add(fieldURI);
            fieldURI = alternativePath(sameAs_fields);
        } else {
            fieldURI = uriToResource(fieldURI);
        }
        String targetName = field.targetType;
        String parentId = field.parentId;
        String nodeId = field.nodeId;

        //ToDo: Temporal fix by setting limit for subqueries to an empty string
        // -> Problem by limit and offset: if applied the limitation is not on a per subject entity basis but limits all
        // results leading to incomplete results. Possible solve: Querying all entities separately.
        // Analyze tradeoff between more results and more or mor complex queries.
        String limitOffsetSTR = "";//field.targetType.equals("String") ? "" : limitOffsetClause(field);
        String langFilter = langFilterClause(field);   // Add language filter if defined
        String orderSTR = orderClause(field, orderBy);
        String valueSTR = "";
        if (field.args.containsKey(_ID)) {
            Set<Object> uris = getValuesFromArgument(field.args, _ID);
            valueSTR = valuesClause(nodeId, objectSetToStringSet(uris));
        }

        String fieldPattern = "";
        String rest = "";

        if (filter != null) {
            String filterClause = filterClause(field);
            if (filterClause != null && !filterClause.isEmpty()) {
                filter.add(filterClause);
            }
        }

        if (targetName.equals(HGQL_SCALAR_LITERAL_GQL_NAME)) {
            // field queries the String placeholder object -> query directly the string/Literal and ignore the subfields and type checking for the object
            fieldPattern = toTriple(toVar(parentId), fieldURI, toVar(nodeId));
            rest = isLiteralClause(nodeId);
            // overwrite the field arguments with the literal value arguments
            Query literal_value = field.fields;
            for (int i = 0; i < ((SubQueriesPattern) literal_value).subqueries.size(); i++) {
                QueryPattern query = ((SubQueriesPattern) literal_value).subqueries.get(i);
                if (query.name.equals(HGQL_SCALAR_LITERAL_VALUE_GQL_NAME)) {
                    literal_value = query;
                    break;
                }
            }
            langFilter = langFilterClause((QueryPattern) literal_value);
            limitOffsetSTR = limitOffsetClause((QueryPattern) literal_value);
            orderSTR = orderClause((QueryPattern) literal_value, null);
        } else {
            String typeURI = (schema.getTypes().containsKey(targetName)) ? schema.getTypes().get(targetName).getId() : "";  // If the output type (targetName) is a type of the schema then typeURI is the Id of this type
            if (hasSameAsTypes(targetName)) {
                Set<String> values = getSameAsTypes(targetName);
                values.add(targetName);
                values = values.stream()
                        .map(s -> schema.getTypes().get(s).getId())
                        .collect(Collectors.toSet());
                String var_sameas = SAMEAS + "_" + nodeId;
                String value = valuesClause(var_sameas, values);
                fieldPattern = value + fieldPattern(parentId, nodeId, fieldURI, toVar(var_sameas));
            } else {
                fieldPattern = fieldPattern(parentId, nodeId, fieldURI, typeURI.equals("") ? "" : uriToResource(typeURI));  // SPARQL query for only the field
            }

            rest = getSubQueries(field.fields, rootValues, orderBy, filter);   // SPARQL query for the SelectionSet of the field (subfields) //TODO CHECK THIS ONE TOO FOR FILTER
        }

        String selectField;
        if (!EMPTY_STRING.equals(limitOffsetSTR) || !EMPTY_STRING.equals(orderSTR) || !EMPTY_STRING.equals(valueSTR)) {   // Select wrapping is only needed if limit, offset, order or _id restrictions are defined
            selectField = "{ " + selectQueryClause(Stream.of(rootValues, valueSTR, fieldPattern, langFilter).filter(Objects::nonNull).collect(Collectors.joining(""))) + orderSTR + limitOffsetSTR + " }" + rest;
        } else {
            selectField = fieldPattern + langFilter + rest;
        }

        boolean filterNonExisting = false;
        if (field.args.containsKey(EQUALS_ARGUMENT)) {
            Set<Object> argumentsIter = getValuesFromArgument(field.args, EQUALS_ARGUMENT);
            filterNonExisting = argumentsIter.size() > 0 && argumentsIter.stream().noneMatch(Objects::nonNull);
        }

        if (filterNonExisting) {
            return filterNotExists(selectField);
        }

        if (field.args.containsKey(_ID)) {
            Set<Object> urisIter = getValuesFromArgument(field.args, _ID);
            if (urisIter.size() > 0) {
                return noOptionalClause(selectField);
            }
        }

        return optionalClause(selectField); // Whole query for the field
    }

    /**
     * Generates for each field of the given subfields a corresponding SPARQL query.
     *
     * @param subfields
     * @return
     */
    private String getSubQueries(SubQueriesPattern subfields, String rootValues, List<String> orderBy, List<String> filter) {

        if (subfields == null || subfields.subqueries == null || subfields.subqueries.isEmpty()) {
            return "";
        }
        StringBuilder whereClause = new StringBuilder();
        for (QueryPattern field : subfields.subqueries) {
            whereClause.append(getFieldSubquery(field, rootValues, orderBy, filter));
        }
        return whereClause.toString();
    }

    /**
     * Returns the graph of the service that is responsible for the given queryField. If the responsible service is a
     * ManifoldService then select the service by the given serviceId.
     *
     * @param queryField queryField must be the root field
     * @param serviceId
     * @return
     */
    private String getGraphId(QueryPattern queryField, String serviceId) {
        Service service = schema.getQueryFields().get(queryField.name).service();
        if (service instanceof ManifoldService) {
            service = ((ManifoldService) service).getService(serviceId);
        }
        return ((SPARQLEndpointService) service).getGraph();  // The Graph is defined over the HGQL Schema directive service
    }

    private boolean hasSameAsTypes(String targetName) {
        if (schema.getTypes().containsKey(targetName)) {
            return !schema.getTypes().get(targetName).getSameAs().isEmpty();
        }
        // Given targetName is NOT a type of the schema
        return false;
    }

    private Set<String> getSameAsTypes(String targetName) {
        if (schema.getTypes().containsKey(targetName)) {
            return schema.getTypes().get(targetName).getSameAs();
        }
        // Given targetName is NOT a type of the schema
        return null;
    }

    private boolean hasSameAsFields(String targetName) {
        if (schema.getFields().containsKey(targetName)) {
            return !schema.getFields().get(targetName).getSameAs().isEmpty();
        }
        // Given targetName is NOT a type of the schema
        return false;
    }

    private Set<String> getSameAsFields(String targetName) {
        if (schema.getFields().containsKey(targetName)) {
            return schema.getFields().get(targetName).getSameAs();
        }
        // Given targetName is NOT a type of the schema
        return null;
    }

    /**
     * Builds a property path with the given nodes as alternative paths.
     *
     * @param nodes
     * @return
     */
    private String alternativePath(Set<String> nodes) {
        return nodes.stream()
                .map(t -> "<" + t + ">")
                .collect(Collectors.joining("|"));
    }


}
