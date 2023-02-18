package org.hypergraphql.authentication;

public class Prop {

    private boolean required;
    private boolean multiple;
    private ModelTypes type;
    private Rules rules;

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

    public ModelTypes getType() {
        return type;
    }

    public void setType(ModelTypes type) {
        this.type = type;
    }

    public Rules getRules() {
        return rules;
    }

    public void setRules(Rules rules) {
        this.rules = rules;
    }
}
