package org.hypergraphql.util;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.riot.web.HttpOp;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.hypergraphql.datafetching.services.SPARQLEndpointService;

public class SPARQLExecutionUtils {

    public static boolean ask(SPARQLEndpointService service, String sparqlIdentifier) {

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        Credentials credentials =
                new UsernamePasswordCredentials(service.getUser(), service.getPassword());
        credsProvider.setCredentials(AuthScope.ANY, credentials);
        HttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        HttpOp.setDefaultHttpClient(httpclient);

        ARQ.init();

        String formattedQuery = String.format("ASK  { <%s> ?b  ?c }", sparqlIdentifier);
        org.apache.jena.query.Query jenaQuery = QueryFactory.create(formattedQuery);

        QueryEngineHTTP qEngine = QueryExecutionFactory.createServiceRequest(service.getUrl(), jenaQuery);
        qEngine.setClient(httpclient);

        boolean resultsSet = qEngine.execAsk();

        qEngine.close();
        if (!qEngine.isClosed()) {
            qEngine.abort();
        }
        return resultsSet;

    }
}
