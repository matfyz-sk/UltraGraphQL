package org.hypergraphql.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

public class AuthorizationResolver {

    private final HashMap<String, Model> classes = new HashMap<>();

    public AuthorizationResolver(String modelJson) {

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

        JsonNode subClassOfJsonNode = classNode.get("subclassOf");
        String subClassOf = subClassOfJsonNode != null ? subClassOfJsonNode.get(0).asText() : null;

        Set<String> subClasses = iterateThroughJsonList(classNode.get("subclasses"), JsonNode::asText);
        HashMap<String, Prop> props = createPropsFromJson(classNode.get("props"));

        Policies policies = createPoliciesForClassModel(classNode);

        classModel.setType(className);
        classModel.setSubclasses(subClasses);
        classModel.setSubclassOf(subClassOf);
        classModel.setProps(props);
        classModel.setPolicies(policies);

        return classModel;
    }

    /**
     * Inherit show (get) operation rules in case the modification operation does not have the rules.
     * In case the rules are not defined then there are no restrictions, therefore rather use the get operation restrictions.
     */
    private Policy inheritShowOperationRules(Policy policy, Policy showPolicy) {
        if (policy == null) {
            return showPolicy;
        }
        return policy;
    }

    private Policies createPoliciesForClassModel(JsonNode classJsonNode) {
        Policies policies = new Policies();

        Policy showPolicy = createPolicy(classJsonNode.get("show"));
        Policy createPolicy = inheritShowOperationRules(createPolicy(classJsonNode.get("create")), showPolicy);
        Policy changePolicy = inheritShowOperationRules(createPolicy(classJsonNode.get("change")), showPolicy);
        Policy deletePolicy = inheritShowOperationRules(createPolicy(classJsonNode.get("delete")), showPolicy);

        JsonNode courseInstanceJsonNode = classJsonNode.get("courseInstance");
        if (courseInstanceJsonNode != null) {
            //TODO add logic for courseInstance and merge it with previous one
        }

        policies.setGet(showPolicy);
        policies.setInsert(createPolicy);
        policies.setUpdate(changePolicy);
        policies.setDelete(deletePolicy);

        return policies;
    }

    private Policy createPolicy(JsonNode policyJsonNode) {
        if (policyJsonNode == null) {
            return null;
        }
        Policy policy = new Policy();
        Iterator<JsonNode> policiesJsonNodeIterator = policyJsonNode.elements();

        while (policiesJsonNodeIterator.hasNext()) {
            JsonNode currentPolicyNameJsonNode = policiesJsonNodeIterator.next();
            if (ModelJsonPolicyTypes.ALL.name().equals(currentPolicyNameJsonNode.asText().toUpperCase())) {
                return null;
            }
        }
        return policy;
    }

    private Policies createPoliciesForPropModel(JsonNode propsJsonNode) {
        Policies policies = new Policies();

        //TODO rework this attributes to match with the one in prop attributes
        Policy showPolicy = createPolicy(propsJsonNode.get("show"));
        Policy createPolicy = createPolicy(propsJsonNode.get("create"));
        Policy changePolicy = createPolicy(propsJsonNode.get("change"));
        Policy deletePolicy = createPolicy(propsJsonNode.get("delete"));

        policies.setGet(showPolicy);
        policies.setInsert(createPolicy);
        policies.setUpdate(changePolicy);
        policies.setDelete(deletePolicy);

        return policies;
    }

    private HashMap<String, Prop> createPropsFromJson(JsonNode jsonNode) {
        if (jsonNode == null) {
            return new HashMap<>();
        }

        HashMap<String, Prop> props = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> jsonNodeIterator = jsonNode.fields();

        while (jsonNodeIterator.hasNext()) {
            Map.Entry<String, JsonNode> propJsonEntry = jsonNodeIterator.next();
            //objectMapper.readValue(prop, Prop.class);

            JsonNode propJsonNode = propJsonEntry.getValue();

            boolean required = propJsonNode.get("required").asBoolean();
            boolean multiple = propJsonNode.get("multiple").asBoolean();
            String dataType = propJsonNode.get("dataType").asText();
            JsonNode objectClass = propJsonNode.get("objectClass");
            String type = objectClass != null ? objectClass.asText() : dataType;
            Policies policies = createPoliciesForPropModel(propJsonNode);

            Prop propObject = new Prop();
            propObject.setRequired(required);
            propObject.setMultiple(multiple);
            propObject.setType(type);
            propObject.setPolicies(policies);

            props.put(propJsonEntry.getKey(), propObject);
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

        Policy policyToApply = getSpecificPolicies(method, classObject.getType());

        /* No policy specified, action can be performed by anyone. */
        if (policyToApply == null) {
            return true;
        }

        //TODO apply policy here
        /*if (policyToApply.contains(SUPERADMIN)) {
            return false;
        }*/

        return false;
    }

    public Policy getSpecificPolicies(Method method, String className) {
        if (className == null || method == null) {
            return null;
        }

        Model specificClass = getSpecificClass(className);
        if (specificClass == null) {
            return null;
        }

        Policies policy = specificClass.getPolicies();
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
