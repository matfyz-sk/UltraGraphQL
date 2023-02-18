package org.hypergraphql.authentication;

import org.hypergraphql.model.Course;
import org.hypergraphql.model.CourseInstance;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.hypergraphql.authentication.PolicyTypes.SUPERADMIN;

public class AuthenticationResolver {

    public static HashMap<Class<?>, Model> getInstanceOfEveryClass() {
        HashMap<Class<?>, Model> hashMap = new HashMap<>();

        hashMap.put(Course.class, new Course());
        hashMap.put(CourseInstance.class, new CourseInstance());

        return hashMap;
    }

    public static Model getSpecificClass(Class<?> className){
        return getInstanceOfEveryClass().getOrDefault(className, null);
    }

    public static Set<Class<?>> getBaseDataTypes() {
        return new HashSet<>(Arrays.asList(
                String.class,
                Integer.class,
                BigDecimal.class
        ));
    }

    public <T extends Model> boolean resolve(UserData userData, T classObject, Method method) {
        if (userData.isSuperAdmin()) {
            return true;
        }

        Set<String> policyToApply = getSpecificPolicies(method, classObject);

        if (policyToApply.contains(SUPERADMIN)) {
            return false;
        }

        //TODO continue
        return false;
    }

    public Set<String> getSpecificPolicies(Method method, Model classObject) {
        if (classObject == null || method == null) {
            return null;
        }

        Policies policy = classObject.getPolicies();
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

}
