package org.hypergraphql.enums;

public enum ResponseCode {

    OK(200),
    NO_CONTENT(204),
    FORBIDDEN(403),
    NOT_FOUND(404);

    private final Integer code;

    ResponseCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
