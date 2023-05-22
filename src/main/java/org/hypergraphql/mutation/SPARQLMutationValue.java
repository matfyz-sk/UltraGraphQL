package org.hypergraphql.mutation;

import graphql.language.StringValue;
import graphql.language.Value;

public class SPARQLMutationValue {

    private String translatedMutation;
    private StringValue id;

    public SPARQLMutationValue(String translatedMutation, StringValue id) {
        this.translatedMutation = translatedMutation;
        this.id = id;
    }

    public String getTranslatedMutation() {
        return translatedMutation;
    }

    public void setTranslatedMutation(String translatedMutation) {
        this.translatedMutation = translatedMutation;
    }

    public StringValue getId() {
        return id;
    }

    public void setId(StringValue id) {
        this.id = id;
    }
}
