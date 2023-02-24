package org.hypergraphql.authentication;

public class Prop {

    private boolean required;
    private boolean multiple;
    private String type;
    private Policies policy;

    public Prop(boolean required, boolean multiple, String type, Policies policy) {
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
}
