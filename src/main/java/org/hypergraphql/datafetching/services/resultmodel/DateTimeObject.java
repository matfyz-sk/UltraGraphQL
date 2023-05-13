package org.hypergraphql.datafetching.services.resultmodel;

public class DateTimeObject {

    private Long millis;
    private String representation;

    public Long getMillis() {
        return millis;
    }

    public void setMillis(Long millis) {
        this.millis = millis;
    }

    public String getRepresentation() {
        return representation;
    }

    public void setRepresentation(String representation) {
        this.representation = representation;
    }
}
