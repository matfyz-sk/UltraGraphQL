package org.hypergraphql.util;

import org.hypergraphql.datafetching.services.SPARQLEndpointService;
import org.hypergraphql.datafetching.services.Service;

import java.util.Map;

public class UIDUtils {


    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int IDENTIFIER_LENGTH = 5;

    public static String next(String classIRI, Map<String, String> prefixes, Service service) {
        String ontologyPrefix = prefixes.get(GlobalValues.COURSES_ONTOLOGY_UGQL_PREFIX);
        String className = classIRI.replace(ontologyPrefix, "");

        String nextId = createNext(className, prefixes);

        while (SPARQLExecutionUtils.ask((SPARQLEndpointService) service, nextId)) {
            nextId = createNext(className, prefixes);
        }
        return nextId;
    }

    public static String createNext(String className, Map<String, String> prefixes) {
        StringBuilder rtn = new StringBuilder();
        for (int i = 0; i < IDENTIFIER_LENGTH; i++) {
            rtn.append(ALPHABET.charAt((int) Math.floor(Math.random() * ALPHABET.length())));
        }
        return String.join("/", prefixes.get(GlobalValues.COURSES_DATA_UGQL_PREFIX), className.toLowerCase(), rtn.toString());
    }

}
