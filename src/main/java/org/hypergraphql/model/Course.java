package org.hypergraphql.model;

import org.hypergraphql.authentication.Model;
import org.hypergraphql.authentication.Policies;
import org.hypergraphql.authentication.Prop;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.hypergraphql.authentication.PolicyTypes.SUPERADMIN;

public final class Course extends Model {

    @Override
    protected Policies classPolicies() {
        return new Policies(null, Collections.singletonList(SUPERADMIN), Collections.singletonList(SUPERADMIN), Collections.singletonList(SUPERADMIN));
    }

    @Override
    protected List<Class<?>> subclasses() {
        return Collections.singletonList(CourseInstance.class);
    }

    @Override
    protected HashMap<String, Prop<?>> props() {
        HashMap<String, Prop<?>> hashMap = new HashMap<>();

        hashMap.put("name" , prop(true, false, String.class, null));
        hashMap.put("description" , prop(false, false, String.class, null));
        hashMap.put("abbreviation" , prop(false, false, String.class, null));
        hashMap.put("hasPrerequisite" , prop(false, true, Course.class, null));
        hashMap.put("mentions" , prop(false, true, Topic.class, null));
        hashMap.put("covers" , prop(false, true, Topic.class, null));
        hashMap.put("hasAdmin" , prop(false, true, User.class, null));

        return hashMap;
    }

}
