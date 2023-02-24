package org.hypergraphql.authentication;

import java.util.HashMap;
import java.util.Set;

public class Model {

    private Set<String> subclasses;
    private String type;
    private Policies policy;
    private HashMap<String, Prop> props;

    protected Model(Set<String> subclasses, Policies policy, HashMap<String, Prop> props) {
        this.subclasses = subclasses;
        this.policy = policy;
        this.props = props;
    }

    public Set<String> getSubclasses() {
        return subclasses;
    }

    public void setSubclasses(Set<String> subclasses) {
        this.subclasses = subclasses;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Policies getPolicy() {
        return policy;
    }

    public void setPolicy(Policies policy) {
        this.policy = policy;
    }

    public HashMap<String, Prop> getProps() {
        return props;
    }

    public void setProps(HashMap<String, Prop> props) {
        this.props = props;
    }
}
