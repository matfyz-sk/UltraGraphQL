package org.hypergraphql.authentication;

public class Policies {

    private Policy get;
    private Policy insert;
    private Policy update;
    private Policy delete;

    public Policy getGet() {
        return get;
    }

    public void setGet(Policy get) {
        this.get = get;
    }

    public Policy getInsert() {
        return insert;
    }

    public void setInsert(Policy insert) {
        this.insert = insert;
    }

    public Policy getUpdate() {
        return update;
    }

    public void setUpdate(Policy update) {
        this.update = update;
    }

    public Policy getDelete() {
        return delete;
    }

    public void setDelete(Policy delete) {
        this.delete = delete;
    }
}
