package org.hypergraphql.authentication.model;

import org.hypergraphql.authentication.Model;
import org.hypergraphql.authentication.Policies;
import org.hypergraphql.authentication.Prop;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.hypergraphql.authentication.PolicyTypes.AUTHOR;
import static org.hypergraphql.authentication.PolicyTypes.SUPERADMIN;

public final class Agent extends Model {

    @Override
    protected Policies classPolicies() {
        return new Policies(Collections.singletonList(SUPERADMIN), Collections.singletonList(SUPERADMIN), Collections.singletonList(SUPERADMIN), Collections.singletonList(SUPERADMIN));
    }

    @Override
    protected List<Class<?>> subclasses() {
        return Arrays.asList(User.class, Team.class);
    }

    @Override
    protected HashMap<String, Prop<?>> props() {
        HashMap<String, Prop<?>> hashMap = new HashMap<>();

        hashMap.put("avatar", prop(false, false, String.class, new Policies(Collections.singletonList(AUTHOR), Collections.singletonList(AUTHOR), Collections.singletonList(AUTHOR), Collections.singletonList(AUTHOR))));

        return hashMap;
    }

}
