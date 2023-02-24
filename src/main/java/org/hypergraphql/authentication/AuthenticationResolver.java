package org.hypergraphql.authentication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

import static org.hypergraphql.authentication.PolicyTypes.SUPERADMIN;

public class AuthenticationResolver {

    private final HashMap<String, Model> classes;

    public AuthenticationResolver(String modelJson) {

        File jsonFile = new File(modelJson);
        ObjectMapper objectMapper = new ObjectMapper();

        classes = new HashMap<>();

        try {
            JsonNode rootNode = objectMapper.readTree(jsonFile);
            Iterator<JsonNode> jsonNodeIterator = rootNode.elements();

            while(jsonNodeIterator.hasNext()){
                JsonNode currentClass = jsonNodeIterator.next();

                System.out.println(currentClass);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Model getSpecificClass(String className) {
        return getClasses().getOrDefault(className, null);
    }

    public static Set<Class<?>> getBaseDataTypes() {
        return new HashSet<>(Arrays.asList(
                String.class,
                Integer.class,
                BigDecimal.class
        ));
    }

    public boolean resolve(UserData userData, Model classObject, Method method) {
        if (userData.isSuperAdmin()) {
            return true;
        }

        Set<String> policyToApply = getSpecificPolicies(method, classObject.getType());

        if (policyToApply.contains(SUPERADMIN)) {
            return false;
        }

        //TODO continue
        return false;
    }

    public Set<String> getSpecificPolicies(Method method, String className) {
        if (className == null || method == null) {
            return null;
        }

        Model specificClass = getSpecificClass(className);
        if (specificClass == null) {
            return null;
        }

        Policies policy = specificClass.getPolicy();
        switch (method) {
            case GET:
                return policy.getGet();
            case INSERT:
                return policy.getInsert();
            case UPDATE:
                return policy.getUpdate();
            case DELETE:
                return policy.getDelete();
        }
        return null;
    }

    public HashMap<String, Model> getClasses() {
        return classes;
    }
}
