package org.hypergraphql.exception;

public class GraphQLIllegalArgumentException extends IllegalArgumentException {

    public GraphQLIllegalArgumentException(String message) {
        super(message);
    }

    public GraphQLIllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
