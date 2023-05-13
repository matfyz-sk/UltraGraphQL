package org.hypergraphql.config.system;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class GraphqlConfig {

    private final Integer port;
    private final String graphqlPath;
    private final String graphiqlPath;
    private final String serverFramework;

    @JsonCreator
    public GraphqlConfig(@JsonProperty("port") Integer port,
                         @JsonProperty("graphql") String graphqlPath,
                         @JsonProperty("graphiql") String graphiqlPath,
                         @JsonProperty("framework") String serverFramework
    ) {
        this.port = Objects.requireNonNullElseGet(port, this::generateRandomPort);
        this.graphqlPath = graphqlPath;
        this.graphiqlPath = graphiqlPath;
        this.serverFramework = serverFramework;
    }

    public Integer port() {
        return port;
    }

    public String graphQLPath() {
        return graphqlPath;
    }

    public String graphiQLPath() {
        return graphiqlPath;
    }

    @JsonIgnore
    private int generateRandomPort() {
        int min = 1024;
        int max = 65536;
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public String serverFramwork() {
        return serverFramework;
    }
}
