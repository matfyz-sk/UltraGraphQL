package org.hypergraphql.util;

public class BaseUtils {

    private BaseUtils() {

    }

    public static String createObjectKeyWithoutKnownPrefixes(String baseKey) {
        if (baseKey == null || baseKey.isEmpty()) {
            return baseKey;
        }
        return baseKey.replace(GlobalValues.COURSES_ONTOLOGY_UGQL_PREFIX_WITH_UNDERSCORE, GlobalValues.EMPTY_STRING).replace(GlobalValues.COURSES_DATA_UGQL_PREFIX_WITH_UNDERSCORE, GlobalValues.EMPTY_STRING);
    }

    /**
     * In case a model class should start with lowerCase then use this method.
     */
    public static String createObjectKeyWithoutKnownPrefixesFirstLowerCase(String baseKey) {
        if (baseKey == null || baseKey.isEmpty()) {
            return baseKey;
        }
        char[] keyWithoutPrefix = baseKey.replace(GlobalValues.COURSES_ONTOLOGY_UGQL_PREFIX_WITH_UNDERSCORE, GlobalValues.EMPTY_STRING).replace(GlobalValues.COURSES_DATA_UGQL_PREFIX_WITH_UNDERSCORE, GlobalValues.EMPTY_STRING).toCharArray();
        keyWithoutPrefix[0] = Character.toLowerCase(keyWithoutPrefix[0]);
        return new String(keyWithoutPrefix);
    }

}
