package org.hypergraphql.mutation;

import graphql.language.StringValue;

public class SPARQLMutationValue {

    private MutationAction mutationAction;
    private String translatedMutation;
    private StringValue id;

    public SPARQLMutationValue(String translatedMutation, StringValue id, MutationAction mutationAction) {
        this.translatedMutation = translatedMutation;
        this.id = id;
        this.mutationAction = mutationAction;
    }

    public MutationAction getMutationAction() {
        return mutationAction;
    }

    public void setMutationAction(MutationAction mutationAction) {
        this.mutationAction = mutationAction;
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
