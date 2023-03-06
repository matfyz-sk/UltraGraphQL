package org.hypergraphql.util;

import java.util.Map;

public class UID {

    private static final String COURSES_ONTOLOGY_UGQL_PREFIX = "courses";
    private static final String COURSES_DATA_UGQL_PREFIX = "courses-data";
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int IDENTIFIER_LENGTH = 5;

    public static String next(String classIRI, Map<String, String> prefixes) {
        String ontologyPrefix = prefixes.get(COURSES_ONTOLOGY_UGQL_PREFIX);
        String className = classIRI.replace(ontologyPrefix, "");

        StringBuilder rtn = new StringBuilder();
        for (int i = 0; i < IDENTIFIER_LENGTH; i++) {
            rtn.append(ALPHABET.charAt((int) Math.floor(Math.random() * ALPHABET.length())));
        }
        return String.join("/", prefixes.get(COURSES_DATA_UGQL_PREFIX), className.toLowerCase(), rtn.toString());
    }

}
