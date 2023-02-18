package org.hypergraphql.authentication;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Policies {

    private Set<String> get;
    private Set<String> insert;
    private Set<String> update;
    private Set<String> delete;

    public Policies(List<String> get, List<String> insert, List<String> update, List<String> delete) {
        this.get = transferListToSet(get);
        this.insert = transferListToSet(insert);
        this.update = transferListToSet(update);
        this.delete = transferListToSet(delete);
    }

    private Set<String> transferListToSet(List<String> list) {
        return list == null ? new HashSet<>() : new HashSet<>(list);
    }

    public Set<String> getGet() {
        return get;
    }

    public void setGet(Set<String> get) {
        this.get = get;
    }

    public Set<String> getInsert() {
        return insert;
    }

    public void setInsert(Set<String> insert) {
        this.insert = insert;
    }

    public Set<String> getUpdate() {
        return update;
    }

    public void setUpdate(Set<String> update) {
        this.update = update;
    }

    public Set<String> getDelete() {
        return delete;
    }

    public void setDelete(Set<String> delete) {
        this.delete = delete;
    }
}
