package org.hypergraphql.config.schema;

import graphql.schema.GraphQLOutputType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static graphql.Scalars.*;
import static graphql.scalars.ExtendedScalars.*;
import static org.hypergraphql.schemaextraction.ExtendedScalars.GraphQLDateTime;
import static org.hypergraphql.util.GlobalValues._ID;
import static org.hypergraphql.util.GlobalValues._TYPE;

public class HGQLVocabulary {


    /* These types should be also used in mapping @see Service#buildModel */
    public static final String SCALAR_STRING = "String";
    public static final String SCALAR_INT = "Int";
    public static final String SCALAR_BOOLEAN = "Boolean";
    public static final String SCALAR_FLOAT = "Float";
    public static final String SCALAR_DECIMAL = "Decimal";
    public static final String SCALAR_LONG = "Long";
    public static final String SCALAR_SHORT = "Short";
    public static final String SCALAR_DATETIME = "DateTime";
    public static final String SCALAR_ID = "ID";

    public static final String HGQL_PREFIX = "hgql:";
    public static final String HGQL_SCHEMA_PREFIX = "hgql-schema:";
    public static final String HGQL_NAMESPACE = "http://hypergraphql.org/";
    public static final String HGQL_SCHEMA_NAMESPACE = HGQL_NAMESPACE + "schema/";
    public static final String HGQL_SCHEMA_NAMESPACE_PREFIX = "hgqls";
    public static final String HGQL_QUERY_URI = HGQL_NAMESPACE + "query";
    public static final String HGQL_QUERY_NAMESPACE = HGQL_QUERY_URI + "/";
    public static final String HGQL_OBJECT_TYPE = HGQL_NAMESPACE + "ObjectType";
    public static final String HGQL_SCALAR_TYPE = HGQL_NAMESPACE + "ScalarType";

    public static final String HGQL_STRING = HGQL_NAMESPACE + SCALAR_STRING;
    public static final String HGQL_INT = HGQL_NAMESPACE + SCALAR_INT;
    public static final String HGQL_BOOLEAN = HGQL_NAMESPACE + SCALAR_BOOLEAN;
    public static final String HGQL_FLOAT = HGQL_NAMESPACE + SCALAR_FLOAT;
    public static final String HGQL_DECIMAL = HGQL_NAMESPACE + SCALAR_DECIMAL;
    public static final String HGQL_LONG = HGQL_NAMESPACE + SCALAR_LONG;
    public static final String HGQL_SHORT = HGQL_NAMESPACE + SCALAR_SHORT;
    public static final String HGQL_DATETIME = HGQL_NAMESPACE + SCALAR_DATETIME;
    public static final String HGQL_ID = HGQL_NAMESPACE + SCALAR_ID;

    public static final String HGQL_LIST_TYPE = HGQL_NAMESPACE + "ListType";
    public static final String HGQL_NON_NULL_TYPE = HGQL_NAMESPACE + "NonNullType";
    public static final String HGQL_QUERY_TYPE = HGQL_NAMESPACE + "QueryType";
    public static final String HGQL_HREF = HGQL_NAMESPACE + "href";
    public static final String HGQL_SCHEMA = HGQL_NAMESPACE + "Schema";
    public static final String HGQL_FIELD = HGQL_NAMESPACE + "Field";
    public static final String HGQL_QUERY_FIELD = HGQL_NAMESPACE + "QueryField";
    public static final String HGQL_QUERY_GET_FIELD = HGQL_NAMESPACE + "QueryGetField";
    public static final String HGQL_QUERY_GET_BY_ID_FIELD = HGQL_NAMESPACE + "QueryGetByIdField";
    public static final String HGQL_HAS_FIELD = HGQL_NAMESPACE + "field";
    public static final String HGQL_SERVICE = HGQL_NAMESPACE + "Service";
    public static final String HGQL_HAS_SERVICE = HGQL_NAMESPACE + "service";
    public static final String HGQL_HAS_NAME = HGQL_NAMESPACE + "name";
    public static final String HGQL_HAS_ID = HGQL_NAMESPACE + "id";
    public static final String HGQL_SERVICE_NAMESPACE = HGQL_HAS_SERVICE + "/";
    public static final String HGQL_OUTPUT_TYPE = HGQL_NAMESPACE + "outputType";
    public static final String HGQL_OF_TYPE = HGQL_NAMESPACE + "ofType";
    public static final String HGQL_KIND = HGQL_NAMESPACE + "kind";
    public static final String HGQL_UNION_TYPE = HGQL_NAMESPACE + "UnionType";
    public static final String HGQL_HAS_UNION_MEMBER = HGQL_NAMESPACE + "unionMember";
    public static final String HGQL_INTERFACE_TYPE = HGQL_NAMESPACE + "InterfaceType";
    public static final String HGQL_IMPLEMENTS = HGQL_NAMESPACE + "implements";

    public static final Map<String, String> SCALAR_TYPES = Collections.unmodifiableMap(new HashMap<>() {{
        put(SCALAR_STRING, HGQL_STRING);
        put(SCALAR_INT, HGQL_INT);
        put(SCALAR_BOOLEAN, HGQL_BOOLEAN);
        put(SCALAR_FLOAT, HGQL_FLOAT);
        put(SCALAR_DATETIME, HGQL_DATETIME);
        put(SCALAR_DECIMAL, HGQL_DECIMAL);
        put(SCALAR_LONG, HGQL_LONG);
        put(SCALAR_SHORT, HGQL_SHORT);
        put(SCALAR_ID, HGQL_ID);
    }});

    public static final Map<String, GraphQLOutputType> SCALAR_TYPES_TO_GRAPHQL_OUTPUT =
            Collections.unmodifiableMap(new HashMap<>() {{
                put(HGQL_STRING, GraphQLString);
                put(HGQL_INT, GraphQLInt);
                put(HGQL_BOOLEAN, GraphQLBoolean);
                put(HGQL_FLOAT, GraphQLFloat);
                put(HGQL_DATETIME, GraphQLDateTime);
                put(HGQL_DECIMAL, GraphQLBigDecimal);
                put(HGQL_LONG, GraphQLLong);
                put(HGQL_SHORT, GraphQLShort);
                put(HGQL_ID, GraphQLID);
            }});

    public static final Map<String, String> JSONLD = Collections.unmodifiableMap(new HashMap<>() {{
        put(_ID, "@id");
        put(_TYPE, "@type");
    }});

    // Additions for Automatic Schema Extraction
    public static final String RDF_PREFIX = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String OWL_PREFIX = "http://www.w3.org/2002/07/owl#";
    public static final String RDF_TYPE = RDF_PREFIX + "type";

    public static final String HGQL_SCHEMA_STRING = HGQL_SCHEMA_NAMESPACE + "String";
    public static final String HGQL_SCHEMA_BOOLEAN = HGQL_SCHEMA_NAMESPACE + "Boolean";
    public static final String HGQL_SCHEMA_INTEGER = HGQL_SCHEMA_NAMESPACE + "Int";
    public static final String HGQL_SCHEMA_FLOAT = HGQL_SCHEMA_NAMESPACE + "Float";
    public static final String HGQL_SCHEMA_DATETIME = HGQL_SCHEMA_NAMESPACE + "DateTime";
    public static final String HGQL_SCHEMA_DECIMAL = HGQL_SCHEMA_NAMESPACE + "Decimal";
    public static final String HGQL_SCHEMA_LONG = HGQL_SCHEMA_NAMESPACE + "Long";
    public static final String HGQL_SCHEMA_SHORT = HGQL_SCHEMA_NAMESPACE + "Short";


    /* XML properties starts with lower case */
    public static final String XML = "http://www.w3.org/2001/XMLSchema#";
    public static final String XML_STRING = XML + "string";
    public static final String XML_BOOLEAN = XML + "boolean";
    public static final String XML_INTEGER = XML + "integer";
    public static final String XML_DATETIME = XML + "dateTime";
    public static final String XML_FLOAT = XML + "float";
    public static final String XML_DECIMAL = XML + "decimal";
    public static final String XML_LONG = XML + "long";
    public static final String XML_SHORT = XML + "short";

    public static final String HGQLS_MAPPING = HGQL_NAMESPACE + "mapping/";
    public static final String HGQLS_OBJECT = HGQLS_MAPPING + "object";
    public static final String HGQLS_FIELD = HGQLS_MAPPING + "field";
    public static final String HGQLS_IMPLEMENTS = HGQLS_MAPPING + "implements";
    public static final String HGQLS_IMPLIED_FIELD = HGQLS_MAPPING + "impliedField";
    public static final String HGQLS_FIELD_OBJECT = HGQLS_MAPPING + "fieldObject";
    public static final String HGQLS_FIELD_OUTPUTTYPE = HGQLS_MAPPING + "fieldOutputType";
    public static final String HGQLS_IMPLEMENTS_MUTUALLY = HGQLS_MAPPING + "implementsMutually";
    public static final String HGQLS_SHARED_OUTPUTTYPE = HGQLS_MAPPING + "sharedOutputType";
    public static final String HGQLS_SAME_AS = HGQLS_MAPPING + "sameAs";
    public static final String HGQLS_FUNCTIONAL_PROPERTY = HGQLS_MAPPING + "FunctionalProperty";
    public static final String HGQLS_FUNCTIONAL_DATA_PROPERTY = HGQLS_MAPPING + "FunctionalDataProperty";
    public static final String HGQLS_FUNCTIONAL_OBJECT_PROPERTY = HGQLS_MAPPING + "FunctionalObjectProperty";
    public static final String HGQLS_MIN_CARDINALITY = HGQLS_MAPPING + "minCardinality";

    public static final String HGQL_DIRECTIVE_SCHEMA = "schema";
    public static final String HGQL_DIRECTIVE_PARAMETER_IMPLIED_BY = "impliedBy";
    public static final String HGQL_DIRECTIVE_PARAMETER_SAMEAS = "sameAs";
    public static final String HGQL_DIRECTIVE_SERVICE = "service";
    public static final String HGQL_DIRECTIVE_SERVICE_PARAMETER_ID = "id";

    // class
    public static final String HGQL_QUERY_TEMPLATE_CLASS = "class";
    public static final String HGQL_QUERY_TEMPLATE_CLASSES = "classes";

    public static final String HGQL_QUERY_TEMPLATE_PROPERTY = "property";
    public static final String HGQL_QUERY_TEMPLATE_PROPERTIES = "properties";

    public static final String HGQL_QUERY_TEMPLATE_DOMAIN = "domain";
    public static final String HGQL_QUERY_TEMPLATE_DOMAINS = "domains";

    public static final String HGQL_QUERY_TEMPLATE_RANGE = "range";
    public static final String HGQL_QUERY_TEMPLATE_RANGES = "ranges";

    public static final String HGQL_QUERY_TEMPLATE_SUBCLASSOF = "subClassOf";
    public static final String HGQL_QUERY_TEMPLATE_SUBCLASSESOF = "subClassesOf";

    public static final String HGQL_QUERY_TEMPLATE_SUBPROPERTYOF = "subPropertyOf";
    public static final String HGQL_QUERY_TEMPLATE_SUBPROPERTIESOF = "subPropertiesOf";

    public static final String HGQL_QUERY_TEMPLATE_EQUIVALENTCLASS = "equivalentClass";
    public static final String HGQL_QUERY_TEMPLATE_EQUIVALENTCLASSES = "equivalentClasses";

    public static final String HGQL_QUERY_TEMPLATE_EQUIVALENTPROPERTY = "equivalentProperty";
    public static final String HGQL_QUERY_TEMPLATE_EQUIVALENTPROPERTIES = "equivalentProperties";

    public static final String HGQL_QUERY_TEMPLATE_SAMEAS = "sameAs";
    public static final String HGQL_QUERY_TEMPLATE_SAMEASES = "sameAses";

    // Literal field serves as place holder for scalar string
    public static final String HGQL_SCALAR_LITERAL = "Literal";
    public static final String HGQL_SCALAR_LITERAL_URI = HGQL_SCHEMA_NAMESPACE + HGQL_SCALAR_LITERAL;
    public static final String HGQL_SCALAR_LITERAL_GQL_NAME = HGQL_SCHEMA_NAMESPACE_PREFIX + "_" + HGQL_SCALAR_LITERAL;
    public static final String HGQL_SCALAR_LITERAL_VALUE = "value";
    public static final String HGQL_SCALAR_LITERAL_VALUE_URI = HGQL_SCHEMA_NAMESPACE + HGQL_SCALAR_LITERAL_VALUE;
    public static final String HGQL_SCALAR_LITERAL_VALUE_GQL_NAME = HGQL_SCHEMA_NAMESPACE_PREFIX + "_" + HGQL_SCALAR_LITERAL_VALUE;

    // Mutation
    public static final String HGQL_MUTATION_INPUT = "input";
    public static final String HGQL_MUTATION_INPUT_PREFIX = HGQL_MUTATION_INPUT + "_";
    public static final String HGQL_MUTATION_INPUT_FIELD_INFIX = "_as_";
    public static final String HGQL_MUTATION_INSERT_PREFIX = "insert_";
    public static final String HGQL_MUTATION_UPDATE_PREFIX = "update_";
    public static final String HGQL_MUTATION_DELETE_PREFIX = "delete_";

}


