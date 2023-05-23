package org.hypergraphql.dto;

public class ResponseStatus {

    private final Integer status;

    public ResponseStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
