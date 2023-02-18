package org.hypergraphql.authentication;

public class Prop<T> {

    private boolean required;
    private boolean multiple;
    private Class<T> type;
    private Policies policy;

    public Prop(boolean required, boolean multiple, Class<T> type, Policies policy) {
        this.required = required;
        this.multiple = multiple;
        this.type = type;
        this.policy = policy;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public Policies getRules() {
        return policy;
    }

    public void setRules(Policies policy) {
        this.policy = policy;
    }
}
