package org.hypergraphql.mutation;

import graphql.language.StringValue;
import graphql.language.Value;

public class SPARQLMutationValue {

   private String translatedMutation;
   private Value id;

    public SPARQLMutationValue(String translatedMutation, Value id) {
        this.translatedMutation = translatedMutation;
        this.id = id;
    }

    public String getTranslatedMutation() {
        return translatedMutation;
    }

    public void setTranslatedMutation(String translatedMutation) {
        this.translatedMutation = translatedMutation;
    }

    public Value getId() {
        return id;
    }

    public void setId(StringValue id) {
        this.id = id;
    }
}
