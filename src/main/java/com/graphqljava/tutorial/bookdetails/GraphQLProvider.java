package com.graphqljava.tutorial.bookdetails;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Component
public class GraphQLProvider {
    private GraphQL graphQL;

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }

    @CrossOrigin(origins = "http://localhost:8080", maxAge = 3600)
    @PostConstruct
    public void init() throws IOException {
        URL url = Resources.getResource("schema.graphqls");
        String sdl = Resources.toString(url, Charsets.UTF_8);
        GraphQLSchema graphQLSchema = buildSchema(sdl);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    @Autowired
    GraphQLDataFetchers graphQLDataFetchers;

    private GraphQLSchema buildSchema(String sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    // Adding multiple resolvers:
    //  Refer Default DataFetchers subsection of:
    //      https://www.graphql-java.com/tutorials/getting-started-with-spring-boot/
    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query")
                    .dataFetcher("bookById", graphQLDataFetchers.getBookByIdDataFetcher())
                    .dataFetcher("allBooks", graphQLDataFetchers.getAllBooks())) // added multiple fetchers
                .type(newTypeWiring("Book")
                    .dataFetcher("author", graphQLDataFetchers.getAuthorDataFetcher()))
                .type(newTypeWiring("Author")
                    .dataFetcher("books", graphQLDataFetchers.getBooksByAuthor()))
                .type(newTypeWiring("Mutation")
                    .dataFetcher("addNewBook", graphQLDataFetchers.addNewBook())
                    .dataFetcher("addNewAuthor", graphQLDataFetchers.addNewAuthor()))
                .build();
    }
}