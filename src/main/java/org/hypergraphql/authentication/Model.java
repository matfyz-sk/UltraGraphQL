package org.hypergraphql.authentication;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

abstract public class Model {

    protected Set<Class<?>> subclasses;
    protected Policies policies;
    protected HashMap<String, Prop<?>> props;

    protected Model() {
        this.policies = classPolicies();
        this.props = props();
        this.subclasses = new HashSet<>(subclasses());
    }

    public Set<Class<?>> getSubclasses() {
        return subclasses;
    }

    public Policies getPolicies() {
        return policies;
    }

    public HashMap<String, Prop<?>> getProps() {
        return props;
    }

    protected abstract Policies classPolicies();

    protected abstract List<Class<?>> subclasses();

    protected abstract HashMap<String, Prop<?>> props();

    public Prop<?> prop(boolean required, boolean multiple, Class<?> type, Policies policies) {
        return new Prop<>(required, multiple, type, policies);
    }
}
