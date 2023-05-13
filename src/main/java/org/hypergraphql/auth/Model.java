package org.hypergraphql.auth;

import java.util.HashMap;
import java.util.Set;

public class Model {

    private Set<String> subclasses;
    private String subclassOf;
    private String type;
    private Policies policies;
    private HashMap<String, Prop> props;

    public Set<String> getSubclasses() {
        return subclasses;
    }

    public void setSubclasses(Set<String> subclasses) {
        this.subclasses = subclasses;
    }

    public String getSubclassOf() {
        return subclassOf;
    }

    public void setSubclassOf(String subclassOf) {
        this.subclassOf = subclassOf;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Policies getPolicies() {
        return policies;
    }

    public void setPolicies(Policies policies) {
        this.policies = policies;
    }

    public HashMap<String, Prop> getProps() {
        return props;
    }

    public void setProps(HashMap<String, Prop> props) {
        this.props = props;
    }
}
