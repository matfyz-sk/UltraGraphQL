package org.hypergraphql.authentication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

import static org.hypergraphql.authentication.PolicyTypes.SUPERADMIN;

public class AuthenticationResolver {

    private final HashMap<String, Model> classes = new HashMap<>();

    public AuthenticationResolver(String modelJson) {

        File jsonFile = new File(modelJson);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(jsonFile);
            Iterator<JsonNode> jsonNodeIterator = rootNode.elements();

            while (jsonNodeIterator.hasNext()) {
                JsonNode classNode = jsonNodeIterator.next();
                Model classModel = createClassModel(classNode);
                this.classes.put(classModel.getType(), classModel);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }

    private Model createClassModel(JsonNode classNode) {
        Model classModel = new Model();

        String className = classNode.get("type").get(0).asText();
        Set<String> subClasses = iterateThroughJsonList(classNode.get("subclasses"), JsonNode::asText);
        HashMap<String, Prop> props = createPropsFromJson(classNode.get("props"));

        classModel.setType(className);
        classModel.setSubclasses(subClasses);
        classModel.setProps(props);
        classModel.setPolicy(new Policies(null, null, null, null));

        return classModel;
    }

    private HashMap<String, Prop> createPropsFromJson(JsonNode jsonNode) {
        if (jsonNode == null) {
            return new HashMap<>();
        }

        HashMap<String, Prop> props = new HashMap<>();
        Iterator<JsonNode> jsonNodeIterator = jsonNode.elements();

        while (jsonNodeIterator.hasNext()) {
            JsonNode propJsonNode = jsonNodeIterator.next();
            //objectMapper.readValue(prop, Prop.class);

            boolean required = propJsonNode.get("required").asBoolean();
            boolean multiple = propJsonNode.get("multiple").asBoolean();
            String dataType = propJsonNode.get("dataType").asText();
            JsonNode objectClass = propJsonNode.get("objectClass");
            String type = objectClass != null ? objectClass.asText() : dataType;

            Prop propObject = new Prop();

            propObject.setRequired(required);
            propObject.setMultiple(multiple);
            propObject.setType(type);

            props.put(type, propObject);
        }

        return props;
    }

    private <T> Set<T> iterateThroughJsonList(JsonNode jsonNode, Function<JsonNode, T> className) {
        if (jsonNode == null) {
            return new HashSet<>();
        }

        Set<T> arrayOfValues = new HashSet<>();

        Iterator<JsonNode> jsonIterator = jsonNode.elements();

        while (jsonIterator.hasNext()) {
            JsonNode currentJsonNode = jsonIterator.next();
            arrayOfValues.add(className.apply(currentJsonNode));
        }

        return arrayOfValues;
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
