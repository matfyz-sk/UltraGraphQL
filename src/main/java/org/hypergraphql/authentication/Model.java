package org.hypergraphql.authentication;

import java.util.HashMap;
import java.util.Set;

public class Model {

    private ModelTypes type;
    private Set<ModelTypes> subclasses;
    private Rules rules;
    private HashMap<String, Prop> props;

    public ModelTypes getType() {
        return type;
    }

    public void setType(ModelTypes type) {
        this.type = type;
    }

    public Set<ModelTypes> getSubclasses() {
        return subclasses;
    }

    public void setSubclasses(Set<ModelTypes> subclasses) {
        this.subclasses = subclasses;
    }

    public Rules getRules() {
        return rules;
    }

    public void setRules(Rules rules) {
        this.rules = rules;
    }

    public HashMap<String, Prop> getProps() {
        return props;
    }

    public void setProps(HashMap<String, Prop> props) {
        this.props = props;
    }
}
