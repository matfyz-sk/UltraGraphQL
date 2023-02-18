package org.hypergraphql.model;

import org.hypergraphql.authentication.Model;
import org.hypergraphql.authentication.Policies;
import org.hypergraphql.authentication.Prop;

import java.util.HashMap;
import java.util.List;

public class CourseInstance extends Model {

    @Override
    public Policies classPolicies() {
        return null;
    }

    @Override
    public List<Class<?>> subclasses() {
        return null;
    }

    @Override
    public HashMap<String, Prop<?>> props() {
        return null;
    }
}
