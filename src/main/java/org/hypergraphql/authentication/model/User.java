package org.hypergraphql.authentication.model;

import org.hypergraphql.authentication.Model;
import org.hypergraphql.authentication.Policies;
import org.hypergraphql.authentication.Prop;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.hypergraphql.authentication.PolicyTypes.*;

public final class User extends Model {

    @Override
    protected Policies classPolicies() {
        return new Policies(null, Collections.singletonList(REGISTER), Arrays.asList(SUPERADMIN, AUTHOR), Collections.singletonList(SUPERADMIN));
    }

    @Override
    protected List<Class<?>> subclasses() {
        return null;
    }

    @Override
    protected HashMap<String, Prop<?>> props() {
        return null;
    }
}
