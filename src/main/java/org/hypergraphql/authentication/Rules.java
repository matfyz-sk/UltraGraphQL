package org.hypergraphql.authentication;

import java.util.List;

public class Rules {

    private List<String> insert;
    private List<String> update;
    private List<String> delete;

    public List<String> getInsert() {
        return insert;
    }

    public void setInsert(List<String> insert) {
        this.insert = insert;
    }

    public List<String> getUpdate() {
        return update;
    }

    public void setUpdate(List<String> update) {
        this.update = update;
    }

    public List<String> getDelete() {
        return delete;
    }

    public void setDelete(List<String> delete) {
        this.delete = delete;
    }
}
