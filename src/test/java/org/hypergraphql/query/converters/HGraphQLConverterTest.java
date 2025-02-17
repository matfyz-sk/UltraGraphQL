package org.hypergraphql.query.converters;

import org.hypergraphql.Controller;
import org.hypergraphql.config.system.HGQLConfig;
import org.hypergraphql.services.HGQLConfigService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

//@Disabled
class HGraphQLConverterTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(HGraphQLConverterTest.class);

    private Controller controller;
    private HGQLConfig config;

    @BeforeEach
    void startUp() {

        final String configPath = "test_config.json";
        final HGQLConfigService configService = new HGQLConfigService();
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configPath);
        config = configService.loadHGQLConfig(configPath, inputStream, true); // ???
        if (controller == null) {
            controller = new Controller();
            controller.start(config);
        }
    }

    @AfterEach
    void cleanUp() {

        if (controller != null) {
            controller.stop();
        }
    }

    @Test
    void rewritingValidityOfGet() {

//        String testQuery = "" +
//                "{\n" +
//                "Company_GET(limit:1, offset:3) {\n" +
//                "  name\n" +
//                "  owner {\n" +
//                "    name \n" +
//                "    birthPlace {\n" +
//                "      label(lang:en)\n" +
//                "    }\n" +
//                "  }\n" +
//                "}\n" +
//                "}";
//
//        boolean test = generateRewritingForRootReturnValidity(testQuery);
//        assertTrue(test);
    }


    @Test
    void rewritingValidityOfGetByID() {

//        String testQuery = "" +
//                "{\n" +
//                "Company_GET_BY_ID(uris:[\"http://test1\", \"http://test2\", \"http://test3\"]) {\n" +
//                "  name\n" +
//                "  owner {\n" +
//                "    name \n" +
//                "    birthPlace {\n" +
//                "      label(lang:\"en\")\n" +
//                "    }\n" +
//                "  }\n" +
//                "}\n" +
//                "}";
//
//        boolean test = generateRewritingForRootReturnValidity(testQuery);
//        assertTrue(test);
    }

    @Test
    void rewritingValidityOfNonRootQuery() {
//
//        String query = "" +
//                "{\n" +
//                "  Person_GET(limit:10) {\n" +
//                "    name\n" +
//                "    _id\n" +
//                "    birthDate\n" +
//                "    birthPlace {\n" +
//                "      label(lang:\"en\")\n" +
//                "    }\n" +
//                "  }\n" +
//                "}";
//
//        Set<String> inputSet = new HashSet<String>() {{
//            add("http://test1");
//            add("http://test2");
//            add("http://test3");
//        }};
//
//        boolean test = generateRewritingForNonRootReturnValidity(query, inputSet);
//        assertTrue(test);
    }

    @Test
    void simple_fragment() {
//
//        final String query = "fragment PersonAttrs on Person {\n" +
//                "  prefLabel(lang: \"en\")\n" +
//                "  altLabel\n" +
//                "}\n" +
//                "\n" +
//                "{\n" +
//                "  Person_GET(limit: 10) { \n" +
//                "    _id\n" +
//                "    ...PersonAttrs\n" +
//                "  }\n" +
//                "}";
//
//        assertTrue(generateRewritingForRootReturnValidity(query));
    }

    @Test
    void nested_fragment() {

//        final String query = "fragment PersonAttrs on Person {\n" +
//                "  prefLabel(lang: \"en\")\n" +
//                "  altLabel\n" +
//                "}\n" +
//                "\n" +
//                "fragment PersonRecursive on Person {\n" +
//                "\t...PersonAttrs\n" +
//                "  broader {\n" +
//                "    ...PersonAttrs\n" +
//                "     broader{\n" +
//                "       ...PersonAttrs\n" +
//                "       broader {\n" +
//                "         ...PersonAttrs\n" +
//                "         broader {\n" +
//                "           ...PersonAttrs\n" +
//                "         }\n" +
//                "       }\n" +
//                "     }\n" +
//                "  }\n" +
//                "  narrower {\n" +
//                "    ...PersonAttrs\n" +
//                "  }\n" +
//                "}\n" +
//                "\n" +
//                "{\n" +
//                "  Person_GET(limit: 100) { \n" +
//                "    _id\n" +
//                "  \t...PersonRecursive\n" +
//                "  }\n" +
//                "}";
//
//        assertTrue(generateRewritingForRootReturnValidity(query));
    }

    // TODO - These methods seem a little complicated
//    private boolean generateRewritingForRootReturnValidity(String inputQuery) {
//
//        HGraphQLConverter converter = new HGraphQLConverter(config.getHgqlSchema());
//
//        ValidatedQuery validatedQuery = new QueryValidator(config.getSchema()).validateQuery(inputQuery);
//        ExecutionForest queryExecutionForest = new ExecutionForestFactory().getExecutionForest(validatedQuery.getParsedQuery(), config.getHgqlSchema());
//        ExecutionTreeNode node = queryExecutionForest.getForest().iterator().next();
//        JsonNode query = node.getQuery();
//        String typeName = node.getRootType();
//        String gqlQuery = converter.convertToHGraphQL(query, new HashSet<>(), typeName);
//        LOGGER.debug(gqlQuery);
//        ValidatedQuery testQueryValidation = new QueryValidator(config.getSchema()).validateQuery(gqlQuery);
//
//        return testQueryValidation.getValid();
//    }
//
//    private boolean generateRewritingForNonRootReturnValidity(String inputQuery, Set<String> inputSet) {
//
//        HGraphQLConverter converter = new HGraphQLConverter(config.getHgqlSchema());
//
//        ValidatedQuery validatedQuery = new QueryValidator(config.getSchema()).validateQuery(inputQuery);
//        ExecutionForest queryExecutionForest = new ExecutionForestFactory().getExecutionForest(validatedQuery.getParsedQuery(), config.getHgqlSchema());
//        ExecutionTreeNode node = queryExecutionForest.getForest().iterator().next().getChildrenNodes().get("x_1").getForest().iterator().next();
//        JsonNode query = node.getQuery();
//        String typeName = node.getRootType();
//        String gqlQuery = converter.convertToHGraphQL(query, inputSet, typeName);
//        LOGGER.debug(gqlQuery);
//        ValidatedQuery testQueryValidation = new QueryValidator(config.getSchema()).validateQuery(gqlQuery);
//
//        return testQueryValidation.getValid();
//    }
}