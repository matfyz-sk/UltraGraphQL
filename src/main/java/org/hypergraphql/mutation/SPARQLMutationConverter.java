package org.hypergraphql.mutation;

import graphql.language.*;
import io.micrometer.core.lang.NonNull;
import org.hypergraphql.config.schema.FieldOfTypeConfig;
import org.hypergraphql.config.schema.TypeConfig;
import org.hypergraphql.datafetching.services.SPARQLEndpointService;
import org.hypergraphql.datafetching.services.Service;
import org.hypergraphql.datamodel.HGQLSchema;
import org.hypergraphql.mutation.values.DateTimeValue;
import org.hypergraphql.mutation.values.DecimalValue;
import org.hypergraphql.util.UIDUtils;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.hypergraphql.config.schema.HGQLVocabulary.*;
import static org.hypergraphql.mutation.SPARQLTypeConverter.getIRIType;
import static org.hypergraphql.mutation.SPARQLTypeConverter.getSchemaScalarType;
import static org.hypergraphql.query.converters.SPARQLServiceConverter.*;
import static org.hypergraphql.util.GlobalValues.*;

/**
 * Provides methods to translate GraphQL mutations to corresponding SPARQL actions.
 */
public class SPARQLMutationConverter {
    private final HGQLSchema schema;
    private final Map<String, String> prefixes;
    private final String ontologyPrefix;
    private final String dataPrefix;
    private static final String rdf_type = "a";
    private static final String GENERIC_GRAPH = "test";
    private static final Integer FIRST_INDEX_ATOMIC_INTEGER = 0;

    public SPARQLMutationConverter(HGQLSchema schema, Map<String, String> prefixes) {
        this.schema = schema;
        this.prefixes = prefixes;
        this.ontologyPrefix = prefixes.get(COURSES_ONTOLOGY_UGQL_PREFIX);
        this.dataPrefix = prefixes.get(COURSES_DATA_UGQL_PREFIX);
    }

    /**
     * Translates a given GraphQL mutation into corresponding SPARQL actions.
     * If the given mutation is an insert mutation a SPARQL insert action is created.
     * If the given mutation is a delete mutation a SPARQL delete action is created
     *
     * @param mutation GraphQL mutation
     * @return SPARQL action
     */
    public SPARQLMutationValue translateMutation(Field mutation, Service service) {
        MutationAction action = decideAction(mutation.getName());

        if (action == null) {
            return null;
        }

        return switch (action) {
            case INSERT -> translateInsertMutation(mutation, service);
            case UPDATE -> translateUpdateMutation(mutation);
            case DELETE -> translateDeleteMutation(mutation);
            default -> null;
        };
    }

    public String getOntologyPrefix() {
        return ontologyPrefix;
    }

    public String getDataPrefix() {
        return dataPrefix;
    }

    /**
     * Translates a given mutation into an SPARQL insert containing all information that were provided as triples.
     *
     * @param mutation GraphQL insert mutation
     * @return SPARQL insert action
     */
    private SPARQLMutationValue translateInsertMutation(Field mutation, Service service) {
        TypeConfig rootObject = this.schema.getTypes().get(this.schema.getMutationFields().get(mutation.getName()));
        final List<Argument> args = mutation.getArguments();   // containing the mutation information

        Optional<String> idFromParameter = args.stream()
                .filter(argument -> argument.getName().equals(_ID) && argument.getValue() instanceof StringValue)
                .map(argument -> ((StringValue) argument.getValue()).getValue())
                .findFirst();

        String id = idFromParameter.orElseGet(() -> service instanceof SPARQLEndpointService ? UIDUtils.next(rootObject.getId(), getPrefixes(), service) : null);

        String resourceId = uriToResource(id);
        String result = toTriple(resourceId, rdf_type, uriToResource(rootObject.getId())) + "\n";

        /* createdAt should be added only in case it is a new entity. In case the id is from parameter then it is an existing ID. */
        if (!idFromParameter.isPresent()) {
            result += addCreatedAttributeToResult(resourceId);
        }

        result += args.stream()
                .filter(argument -> !argument.getName().equals(_ID))
                .map(argument -> translateArgument(rootObject, id, argument, MutationAction.INSERT))
                .collect(Collectors.joining("\n"));
        return new SPARQLMutationValue(addSPARQLInsertWrapper(result, getGraphName(getMutationService())), new StringValue(id), MutationAction.INSERT);
    }

    public String addCreatedAttributeToResult(String uriResource) {
        String predicateCreated = uriToResource(getOntologyPrefix() + CREATED_PROP);
        DateTime currentDateTime = new org.joda.time.DateTime().toDateTimeISO();
        return toTriple(uriResource, predicateCreated, getSchemaScalarType(currentDateTime.toString(), DateTimeValue.class)) + "\n";
    }

    /**
     * Translates a given mutation into an SPARQL update containing all information that were provided as triples.
     *
     * @param mutation GraphQL update mutation
     * @return SPARQL update action
     */
    private SPARQLMutationValue translateUpdateMutation(Field mutation) {
        TypeConfig rootObject = this.schema.getTypes().get(this.schema.getMutationFields().get(mutation.getName()));
        final List<Argument> args = mutation.getArguments();   // containing the mutation information

        Optional<String> id = args.stream()
                .filter(argument -> argument.getName().equals(_ID) && argument.getValue() instanceof StringValue)
                .map(argument -> ((StringValue) argument.getValue()).getValue())
                .findFirst();

        if (id.isPresent()) {
            String id_uri = uriToResource(id.get());

            String updateResult = args.stream()
                    .filter(argument -> !argument.getName().equals(_ID))
                    .map(argument -> translateArgument(rootObject, id.get(), argument, MutationAction.UPDATE))
                    .collect(Collectors.joining("\n"));

            List<String> argsToDelete = args.stream().map(Argument::getName).filter(name -> !name.equals(_ID)).toList();

            AtomicInteger i = new AtomicInteger(FIRST_INDEX_ATOMIC_INTEGER);
            List<FieldOfTypeConfig> listOfFieldsToUpdate = rootObject.getFields().values().stream().filter(fieldOfTypeConfig -> !fieldOfTypeConfig.getId().equals(RDF_TYPE) && argsToDelete.contains(fieldOfTypeConfig.getName())).toList();
            String deleteResult = listOfFieldsToUpdate.stream().map(fieldOfTypeConfig -> toTriple(id_uri, uriToResource(fieldOfTypeConfig.getId()), toVar("o_" + i.getAndIncrement())))
                    .collect(Collectors.joining("\n"));

            String classType = toTriple(id_uri, rdf_type, uriToResource(rootObject.getId())) + "\n";

            i.set(FIRST_INDEX_ATOMIC_INTEGER); //reset Atomic Integer to the beginning
            String whereOptional = listOfFieldsToUpdate.stream().map(fieldOfTypeConfig -> optionalClause(toTriple(id_uri, uriToResource(fieldOfTypeConfig.getId()), toVar("o_" + i.getAndIncrement()))))
                    .collect(Collectors.joining("\n"));
            return new SPARQLMutationValue(addSPARQLUpdateWrapper(deleteResult, updateResult, classType + whereOptional, getGraphName(getMutationService())), new StringValue(id.get()), MutationAction.UPDATE);
        }
        return null;
    }

    /**
     * Translates a given mutation into an SPARQL delete action using the provided information.
     *
     * @param mutation GraphQL delete mutation
     * @return SPARQL delete action
     */
    private SPARQLMutationValue translateDeleteMutation(Field mutation) {
        TypeConfig rootObject = this.schema.getTypes().get(this.schema.getMutationFields().get(mutation.getName()));
        final List<Argument> args = mutation.getArguments();   // containing the mutation information

        Optional<String> optionalID = args.stream()
                .filter(argument -> argument.getName().equals(_ID) && argument.getValue() instanceof StringValue)
                .map(argument -> ((StringValue) argument.getValue()).getValue())
                .findFirst();

        boolean hasID = optionalID.isPresent();
        boolean hasOtherFields = args.stream().anyMatch(argument -> !argument.getName().equals(_ID));  // has at least one field different from _id -> all arguments that are not _id

        if (hasID && hasOtherFields) {
            String result = args.stream()
                    .filter(argument -> !argument.getName().equals(_ID))
                    .map(argument -> translateArgument(rootObject, optionalID.get(), argument, MutationAction.DELETE))
                    .collect(Collectors.joining("\n"));
            return new SPARQLMutationValue(addSPARQLDeleteWrapper(result, null, getGraphName(getMutationService())), new StringValue(optionalID.get()), MutationAction.DELETE);

        } else if (hasID) {  //hasID && !hasOtherFields -> ID defined but no other fields present
            String id_uri = uriToResource(optionalID.get());
            AtomicInteger i = new AtomicInteger(FIRST_INDEX_ATOMIC_INTEGER);

            List<FieldOfTypeConfig> fieldOfTypeConfigs = rootObject.getFields().values().stream().filter(fieldOfTypeConfig -> !fieldOfTypeConfig.getId().equals(RDF_TYPE)).toList();
            String delete_field_type = toTriple(id_uri, rdf_type, uriToResource(rootObject.getId())) + "\n";

            String delete_all_type_fields = fieldOfTypeConfigs.stream().map(fieldOfTypeConfig -> toTriple(id_uri, uriToResource(fieldOfTypeConfig.getId()), toVar("o_" + i.getAndIncrement()))).collect(Collectors.joining("\n"));
            i.set(FIRST_INDEX_ATOMIC_INTEGER);  // reset SPARQL variable id

            String deleteFields = String.join(EMPTY_STRING, delete_field_type, delete_all_type_fields);

            String optionalFields = fieldOfTypeConfigs.stream().map(fieldOfTypeConfig -> optionalClause(toTriple(id_uri, uriToResource(fieldOfTypeConfig.getId()), toVar("o_" + i.getAndIncrement())))).collect(Collectors.joining("\n"));
            String whereCondition = String.join(EMPTY_STRING, delete_field_type, optionalFields);

            return new SPARQLMutationValue(addSPARQLDeleteWrapper(deleteFields, whereCondition, getGraphName(getMutationService())), new StringValue(optionalID.get()), MutationAction.DELETE);

        } else if (hasOtherFields) { //!hasID && hasOtherFields -> ID not defined but other fields
            String var_root = rootObject.getName();
            String delete_all_with_id = toTriple(toVar(rootObject.getName()), toVar("p_1"), toVar("o")) + "\n"
                    + toTriple(toVar("s"), toVar("p_2"), toVar(rootObject.getName()));
            String delete_all_with_id_optional = optionalClause(toTriple(toVar(rootObject.getName()), toVar("p_1"), toVar("o"))) + "\n"
                    + optionalClause(toTriple(toVar("s"), toVar("p_2"), toVar(rootObject.getName())));

            String where = toTriple(toVar(var_root), rdf_type, uriToResource(rootObject.getId())) + "\n";
            where += args.stream()
                    .filter(argument -> !argument.getName().equals(_ID))
                    .map(argument -> translateArgument(rootObject, null, argument, MutationAction.DELETE))
                    .collect(Collectors.joining("\n"));
            where += "\n" + delete_all_with_id_optional;

            return new SPARQLMutationValue(addSPARQLDeleteWrapper(delete_all_with_id + "\n", where, getGraphName(getMutationService())), new StringValue(""), MutationAction.DELETE); //TODO return correct ID
        }
        return null;
    }

    private String addSPARQLInsertWrapper(String triples, @NonNull String graph) {
        return String.format("INSERT DATA{\n%s\n}", getGraphPart(triples, graph));
    }

    private String addSPARQLUpdateWrapper(String deleteTriples, String insertTriples, String where, @NonNull String graph) {
        if (where != null) {
            return String.format("WITH<%s>\nDELETE{\n%s}\nINSERT{\n%s}\nWHERE{\n%s\n}", graph, deleteTriples, insertTriples, where);
        }
        return String.format("WITH<%s>\nDELETE{\n%s}\nINSERT{\n%s}", graph, deleteTriples, insertTriples);
    }

    /**
     * Add the SPARQL DELETE clause around the given triples
     *
     * @param triples SPARQL term that is suited inside the DELETE clause
     * @param where   SPARQL where part that is suited inside the DELETE clause
     * @return DELETE clause containing given input
     */
    private String addSPARQLDeleteWrapper(String triples, String where, @NonNull String graph) {
        if (where != null) {
            return String.format("WITH<%s>\nDELETE{\n%s}\nWHERE{\n%s\n}", graph, triples, where);
        }
        return String.format("WITH<%s>\nDELETE{\n%s}", graph, triples);
    }

    private String getGraphPart(String triples, @NonNull String graph) {
        return String.format("GRAPH <%s>{\n%s\n}", graph, triples);
    }

    private Service getMutationService() {
        return schema.getServiceList().values().stream().findFirst().orElse(null);
    }

    private String getGraphName(Service service) {
        if (!(service instanceof SPARQLEndpointService)) {
            return GENERIC_GRAPH;
        }
        return ((SPARQLEndpointService) service).getGraph();
    }

    /**
     * Translates a argument of and mutation field to corresponding triples
     *
     * @param root TypeConfig of the output type of the mutation field
     * @param id   IRI of the object to be inserted
     * @param arg  Argument to be translated
     * @return triples representing the relations between the given id and the values given in the argument.
     */
    private String translateArgument(TypeConfig root, String id, Argument arg, MutationAction action) {
        return translateValue(root, id, arg.getName(), arg.getValue(), action);
    }

    /**
     * Checks the type of the value and calls the corresponding handler
     *
     * @param root  TypeConfig of the object
     * @param id    IRI of object instance of type given in root
     * @param field Field name
     * @param value value associated with the given id via the given field
     * @return triples representing the relations between the given parameters
     */
    private String translateValue(TypeConfig root, String id, String field, Value value, MutationAction action) {
        if (value instanceof ArrayValue) {
            return translateArrayValue(root, id, field, (ArrayValue) value, action);
        } else if (value instanceof ObjectValue) {
            return translateObjectValue(root, id, field, (ObjectValue) value, action);
        } else if (value instanceof StringValue) {
            return translateStringValue(root, id, field, (StringValue) value, getDataPrefix());
        } else if (value instanceof DecimalValue) {
            return translateDecimalValue(root, id, field, (DecimalValue) value);
        } else if (value instanceof IntValue) {
            return translateIntValue(root, id, field, (IntValue) value);
        } else if (value instanceof BooleanValue) {
            return translateBooleanValue(root, id, field, (BooleanValue) value);
        } else if (value instanceof FloatValue) {
            return translateFloatValue(root, id, field, (FloatValue) value);
        } else if (value instanceof DateTimeValue) {
            return translateDateTimeValue(root, id, field, (DateTimeValue) value);
        } else {
            //ToDo: Check if there are more possible values and handle them
            return "";
        }
    }


    /**
     * Translates every item of the given array to corresponding triples.
     *
     * @param root  TypeConfig of the object
     * @param id    IRI of object of type given in root
     * @param field Field name
     * @param value array of values associated with the given id via given field
     * @return triples representing the relations between the given parameters
     */
    private String translateArrayValue(TypeConfig root, String id, String field, ArrayValue value, MutationAction action) {
        return value.getValues().stream()
                .map(val -> translateValue(root, id, field, val, action))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Translates the given ObjectValue as the relation of the given id and the ObjectValue to corresponding triples.
     * If the given ObjectValue only contains the literal placeholders object, then the Strings in the ObjectValue are
     * directly translated to associations between the given id an the strings.
     *
     * @param root  TypeConfig of the object
     * @param id    IRI of object of type given in root
     * @param field Field name
     * @param value ObjectValue linking the given id to another object
     * @return triples representing the relations between the given id and the object given in value as the value it self.
     */
    private String translateObjectValue(TypeConfig root, String id, String field, ObjectValue value, MutationAction action) {
        String field_id = this.schema.getFields().get(this.schema.getInputFields().get(field)).getId();
        List<ObjectField> valueFields = value.getObjectFields();
        if (this.schema.getinputFieldsOutput().get(field).equals(HGQL_SCALAR_LITERAL_GQL_NAME)) {
            //given field has the literal placeholder as output -> only insert the string value
            Value literal_values = valueFields.stream()
                    .filter(objectField -> objectField.getName().equals(HGQL_SCALAR_LITERAL_VALUE_GQL_NAME))
                    .findFirst()
                    .get().getValue();
            return translateValue(root, id, field, literal_values, action);
        }
        final Optional<ObjectField> optional_id = valueFields.stream()
                .filter(objectField -> objectField.getName().equals(_ID))
                .findFirst();
        if (!optional_id.isPresent()) {
            // error id must be present
        }
        String sub_id = ((StringValue) optional_id.get().getValue()).getValue();
        TypeConfig subObject = this.schema.getTypes().get(this.schema.getinputFieldsOutput().get(field));   //output object of the field
        String results = "";
        if (id == null) {
            results += toTriple(toVar(root.getName()), uriToResource(field_id), uriToResource(sub_id)) + "\n";
        } else {
            results += toTriple(uriToResource(id), uriToResource(field_id), uriToResource(sub_id)) + "\n";
        }
        if (action == MutationAction.INSERT || action == MutationAction.UPDATE) {
            results += toTriple(uriToResource(sub_id), rdf_type, uriToResource(subObject.getId())) + "\n";
        }
        results += valueFields.stream()
                .filter(objectField -> !objectField.getName().equals(_ID))
                .map(objectField -> translateValue(subObject, sub_id, objectField.getName(), objectField.getValue(), action))
                .collect(Collectors.joining("\n"));
        return results;
    }

    /**
     * Creates a RDF triple linking a Literal to the given object with the given field/predicate
     *
     * @param subject object IRI
     * @param field   predicate IRI
     * @param value   String literal
     * @return RDF triple
     */
    private String translateStringValue(TypeConfig root, String subject, String field, StringValue value, String ontologyPrefix) {
        String field_id = this.schema.getFields().get(this.schema.getInputFields().get(field)).getId();

        String val = value.getValue();
        boolean containsIriPart = val != null && !val.isEmpty() && val.contains(ontologyPrefix);

        if (subject == null) {
            return toTriple(toVar(root.getName()), uriToResource(field_id), containsIriPart ? getIRIType(val, String.class) : getSchemaScalarType(val, StringValue.class));
        } else {
            return toTriple(uriToResource(subject), uriToResource(field_id), containsIriPart ? getIRIType(val, String.class) : getSchemaScalarType(val, StringValue.class));
        }

    }

    /**
     * Creates a RDF triple linking a Literal to the given object with the given field/predicate
     *
     * @param subject object IRI
     * @param field   predicate IRI
     * @param value   String literal
     * @return RDF triple
     */
    private String translateBooleanValue(TypeConfig root, String subject, String field, BooleanValue value) {
        String field_id = this.schema.getFields().get(this.schema.getInputFields().get(field)).getId();
        if (subject == null) {
            return toTriple(toVar(root.getName()), uriToResource(field_id), getSchemaScalarType(Boolean.toString(value.isValue()), BooleanValue.class));
        } else {
            return toTriple(uriToResource(subject), uriToResource(field_id), getSchemaScalarType(Boolean.toString(value.isValue()), BooleanValue.class));
        }

    }

    /**
     * Creates a RDF triple linking a Literal to the given object with the given field/predicate
     *
     * @param subject object IRI
     * @param field   predicate IRI
     * @param value   String literal
     * @return RDF triple
     */
    private String translateDecimalValue(TypeConfig root, String subject, String field, DecimalValue value) {
        String field_id = this.schema.getFields().get(this.schema.getInputFields().get(field)).getId();
        if (subject == null) {
            return toTriple(toVar(root.getName()), uriToResource(field_id), getSchemaScalarType(value.getValue().toString(), DecimalValue.class));
        } else {
            return toTriple(uriToResource(subject), uriToResource(field_id), getSchemaScalarType(value.getValue().toString(), DecimalValue.class));
        }

    }

    /**
     * Creates a RDF triple linking a Literal to the given object with the given field/predicate
     *
     * @param subject object IRI
     * @param field   predicate IRI
     * @param value   String literal
     * @return RDF triple
     */
    private String translateIntValue(TypeConfig root, String subject, String field, IntValue value) {
        String field_id = this.schema.getFields().get(this.schema.getInputFields().get(field)).getId();
        if (subject == null) {
            return toTriple(toVar(root.getName()), uriToResource(field_id), getSchemaScalarType(value.getValue().toString(), IntValue.class));
        } else {
            return toTriple(uriToResource(subject), uriToResource(field_id), getSchemaScalarType(value.getValue().toString(), IntValue.class));
        }

    }

    /**
     * Creates a RDF triple linking a Literal to the given object with the given field/predicate
     *
     * @param subject object IRI
     * @param field   predicate IRI
     * @param value   String literal
     * @return RDF triple
     */
    private String translateFloatValue(TypeConfig root, String subject, String field, FloatValue value) {
        String field_id = this.schema.getFields().get(this.schema.getInputFields().get(field)).getId();
        if (subject == null) {
            return toTriple(toVar(root.getName()), uriToResource(field_id), getSchemaScalarType(value.getValue().toString(), FloatValue.class));
        } else {
            return toTriple(uriToResource(subject), uriToResource(field_id), getSchemaScalarType(value.getValue().toString(), FloatValue.class));
        }

    }

    /**
     * Creates a RDF triple linking a Literal to the given object with the given field/predicate
     *
     * @param subject object IRI
     * @param field   predicate IRI
     * @param value   String literal
     * @return RDF triple
     */
    //TODO change the representation
    private String translateDateTimeValue(TypeConfig root, String subject, String field, DateTimeValue value) {
        String field_id = this.schema.getFields().get(this.schema.getInputFields().get(field)).getId();
        if (subject == null) {
            return toTriple(toVar(root.getName()), uriToResource(field_id), getSchemaScalarType(value.toString(), DateTimeValue.class));
        } else {
            return toTriple(uriToResource(subject), uriToResource(field_id), getSchemaScalarType(value.toString(), DateTimeValue.class));
        }

    }

    /**
     * Checks which kind of action the given mutation field should perform. The decision is based on the prefix of the field
     *
     * @param mutationField mutation field with an action prefix
     * @return Action the mutation should perform
     */
    private MutationAction decideAction(String mutationField) {
        if (mutationField.startsWith(HGQL_MUTATION_INSERT_PREFIX)) {
            return MutationAction.INSERT;
        } else if (mutationField.startsWith(HGQL_MUTATION_UPDATE_PREFIX)) {
            return MutationAction.UPDATE;
        } else if (mutationField.startsWith(HGQL_MUTATION_DELETE_PREFIX)) {
            return MutationAction.DELETE;
        } else {
            // unknown mutation action
            return null;
        }
    }

    public Map<String, String> getPrefixes() {
        return prefixes;
    }
}
