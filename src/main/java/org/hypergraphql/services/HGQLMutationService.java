package org.hypergraphql.services;

import graphql.*;
import graphql.language.*;
import graphql.schema.GraphQLSchema;
import org.hypergraphql.config.system.HGQLConfig;
import org.hypergraphql.datafetching.ExecutionForest;
import org.hypergraphql.datafetching.ExecutionForestFactory;
import org.hypergraphql.datafetching.services.LocalModelSPARQLService;
import org.hypergraphql.datafetching.services.SPARQLEndpointService;
import org.hypergraphql.datafetching.services.Service;
import org.hypergraphql.datafetching.services.resultmodel.ObjectResult;
import org.hypergraphql.datafetching.services.resultmodel.QueryRootResult;
import org.hypergraphql.datafetching.services.resultmodel.Result;
import org.hypergraphql.datamodel.HGQLSchema;
import org.hypergraphql.dto.ResponseStatus;
import org.hypergraphql.enums.ResponseCode;
import org.hypergraphql.mutation.MutationAction;
import org.hypergraphql.mutation.SPARQLMutationConverter;
import org.hypergraphql.mutation.SPARQLMutationValue;
import org.hypergraphql.query.ValidatedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.hypergraphql.util.GlobalValues.*;

public class HGQLMutationService {
    private final static Logger LOGGER = LoggerFactory.getLogger(HGQLMutationService.class);
    private final HGQLSchema hgqlSchema;
    private final GraphQLSchema schema;
    private final SPARQLMutationConverter converter;
    private final HGQLQueryService query_handler;
    private final GraphQL graphql;
    private final HGQLConfig config;

    private static final int MAX_RETRY_COUNT = 10;

    public HGQLMutationService(HGQLConfig config) {
        this.config = config;
        this.hgqlSchema = config.getHgqlSchema();
        this.schema = config.getSchema();
        this.converter = new SPARQLMutationConverter(this.hgqlSchema, this.config.getPrefixes());
        this.query_handler = new HGQLQueryService(config);
        this.graphql = GraphQL.newGraphQL(config.getSchema()).build();
    }

    public Map<String, Object> results(String request, String acceptType, ValidatedQuery validatedQuery, int retryCount) {

        SelectionSet selectionSet = ExecutionForestFactory.selectionSet(validatedQuery.getParsedQuery());

        final List<Selection> selections = selectionSet.getSelections();

        if (selections == null || selections.isEmpty()) {
            return new HashMap<>();
        }

        Selection<?> selection = selections.get(0);
        List<Field> mutationFields = new ArrayList<>();

        final Service service = this.hgqlSchema.getServiceList().get(this.config.getMutationService());
        SPARQLMutationValue mutationValue = this.converter.translateMutation((Field) selection, service);

        if (mutationValue == null) {
            throw new IllegalArgumentException("Mutation is null");
        }

        LOGGER.info(mutationValue.getTranslatedMutation());
        if (service instanceof LocalModelSPARQLService) {
            ((LocalModelSPARQLService) service).executeUpdate(mutationValue.getTranslatedMutation());
        } else if (service instanceof SPARQLEndpointService) {
            Boolean isPerformed = ((SPARQLEndpointService) service).executeUpdate(mutationValue);

            /* If the transaction fails - is null, generate new request with new ID */
            if (isPerformed == null) {
                if (retryCount + 1 > MAX_RETRY_COUNT) {
                    throw new IllegalArgumentException("The mutation could not be performed. " + request);
                }
                return results(request, acceptType, validatedQuery, retryCount + 1);
            }
        }

        mutationFields.add(Field.newField()
                .name(this.hgqlSchema.getMutationFields().get(((Field) selection).getName()))
                .alias(((Field) selection).getAlias())
                .comments(selection.getComments())
                .arguments(List.of(new Argument(_ID, mutationValue.getId())))
                .directives(((Field) selection).getDirectives())
                .selectionSet(((Field) selection).getSelectionSet())
                .build());

        Document mutationSelectionSets = Document.newDocument()
                .definition(OperationDefinition.newOperationDefinition()
                        .name(OperationDefinition.Operation.QUERY.toString())
                        .operation(OperationDefinition.Operation.QUERY)
                        .selectionSet(SelectionSet.newSelectionSet()
                                .selections(mutationFields)
                                .build())
                        .build())
                .build();

        Map<String, Object> resSelectionSet = executeSelectionSet(request, mutationSelectionSets, acceptType);
        resSelectionSet.put(MUTATION, new ResponseStatus(getResponseCode(mutationValue, resSelectionSet).getCode()));

        return resSelectionSet;
    }

    private ResponseCode getResponseCode(SPARQLMutationValue mutationValue, Map<String, Object> resSelectionSet) {
        if (mutationValue.getMutationAction() == null) {
            return ResponseCode.FORBIDDEN;
        }

        if (getSinglePerformMutationActions().contains(mutationValue.getMutationAction()) && hasSelectionSetData(resSelectionSet)) {
            return ResponseCode.OK;
        }

        if (MutationAction.DELETE == mutationValue.getMutationAction()) {
            //TODO add check when no id is selected (id is null) and the deletion is based on the field - discuss if this functionality is even needed at all.
            return mutationValue.getId() == null || mutationValue.getId().getValue() == null || mutationValue.getId().getValue().isEmpty() || mutationValue.getDeleteResponseSuccessful() ? ResponseCode.OK : ResponseCode.NO_CONTENT;
        }

        return ResponseCode.NO_CONTENT;
    }

    private static List<MutationAction> getSinglePerformMutationActions() {
        return Arrays.asList(
                MutationAction.INSERT,
                MutationAction.UPDATE
        );
    }

    private boolean hasSelectionSetData(Map<String, Object> resSelectionSet) {
        if (resSelectionSet != null && !resSelectionSet.isEmpty()) {
            Object selectionSetObject = resSelectionSet.getOrDefault(DATA, null);
            if (selectionSetObject instanceof HashMap<?, ?> selectionSetHashMap) {
                return selectionSetHashMap.size() > 1;
            }
        }
        return false;
    }

    Map<String, Object> executeSelectionSet(String query, Document document, String acceptType) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<Object, Object> extensions = new HashMap<>();
        List<GraphQLError> errors = new ArrayList<>();

        result.put("errors", errors);
        result.put("extensions", extensions);

        ExecutionInput executionInput;
        ExecutionResult qlResult = null;

        ExecutionForest queryExecutionForest =
                new ExecutionForestFactory().getExecutionForest(document, hgqlSchema);

//        ModelContainer client = new ModelContainer(queryExecutionForest.generateModel());
        Result formattedResult = queryExecutionForest.generateModel();
//        ObjectMapper mapper = new ObjectMapper();
        if (acceptType == null) {
//            executionInput = ExecutionInput.newExecutionInput()
//                    .query(query)
//                    .context(client)
//                    .build();
//
//
//            qlResult = graphql.execute(executionInput);

            if (formattedResult instanceof ObjectResult || formattedResult instanceof QueryRootResult) {
                Map<String, Object> json = (Map<String, Object>) (formattedResult).generateJSON();
                data.putAll(json);
            } else {
                LOGGER.error("Result of query should not be a single JSON Array");
            }
            data.put("@context", queryExecutionForest.getFullLdContext());

            if (data != null) {
                result.put("data", data);
                //ToDo: Add the error messages from the result object
//                errors.addAll(qlResult.getErrors());
            }
        } else {
            result.put("data", formattedResult.generateJSON());
        }
        //ToDo: Improve the Error build-up
        if (formattedResult.getErrors() != null && !formattedResult.getErrors().equals("")) {
            final GraphQLError graphQLError = GraphqlErrorBuilder.newError()
                    .message(formattedResult.getErrors())
                    .build();
            errors.add(graphQLError);
        }

        return result;
    }
}
