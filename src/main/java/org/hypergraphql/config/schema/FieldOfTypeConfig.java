package org.hypergraphql.config.schema;

import graphql.schema.GraphQLOutputType;
import org.hypergraphql.datafetching.services.Service;

public class FieldOfTypeConfig {

    public String getId() {
        return id;
    }

    public Service getService() {
        return service;
    }

    public GraphQLOutputType getGraphqlOutputType() {
        return graphqlOutputType;
    }

    public Boolean isList() {
        return isList;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getName() {
        return name;
    }

    private final String id;   // IRI that is associated with this field
    private final String name;   // name of the IRI in this schema
    private final Service service;   // type of service the field is queryed with e.g. ManifoldService, SPARQLEndpointService, etc.
    private final GraphQLOutputType graphqlOutputType;
    private final boolean isList;
    private final String targetName;

    public FieldOfTypeConfig(String name, String id, Service service, GraphQLOutputType graphqlOutputType, Boolean isList, String targetName) {

        this.name = name;
        this.id = id;
        this.service = service;
        this.graphqlOutputType = graphqlOutputType;
        this.targetName = targetName;
        this.isList = isList;

    }


}
