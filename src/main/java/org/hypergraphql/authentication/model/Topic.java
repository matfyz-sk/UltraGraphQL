package org.hypergraphql.authentication.model;

import org.hypergraphql.authentication.Model;
import org.hypergraphql.authentication.Policies;
import org.hypergraphql.authentication.Prop;

import java.util.HashMap;
import java.util.List;

public final class Topic extends Model {

    @Override
    protected Policies classPolicies() {
        return null;
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
