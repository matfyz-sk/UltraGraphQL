package org.hypergraphql.authentication;

import org.hypergraphql.model.Course;
import org.hypergraphql.model.CourseInstance;

import java.util.HashMap;
import java.util.Set;

import static org.hypergraphql.authentication.PolicyTypes.SUPERADMIN;

public class AuthenticationResolver {

    public static <T extends Model> HashMap<Class<?>, T> getInstanceOfEveryClass() {
        HashMap<Class<?>, T> hashMap = new HashMap<>();

        hashMap.put(Course.class, (T) new Course());
        hashMap.put(CourseInstance.class, (T) new CourseInstance());

        return hashMap;
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

    public <T extends Model> Set<String> getSpecificPolicies(Method method, T classObject) {
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
