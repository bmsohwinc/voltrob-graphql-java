package com.graphqljava.tutorial.bookdetails;

import com.google.common.collect.ImmutableMap;
import graphql.schema.DataFetcher;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class GraphQLDataFetchers {
    private static List<Map<String, String>> books = Arrays.asList(
            ImmutableMap.of(
                    "id", "book-1",
                    "name", "Let Us C",
                    "pageCount", "400",
                    "authorId", "author-1"),
            ImmutableMap.of(
                    "id", "book-2",
                    "name", "Godfather",
                    "pageCount", "300",
                    "authorId", "author-2"),
            ImmutableMap.of(
                    "id", "book-3",
                    "name", "Study in Scarlet",
                    "pageCount", "600",
                    "authorId", "author-3")
    );

    private static List<Map<String, String>> authors = Arrays.asList(
            ImmutableMap.of("id", "author-1",
                    "firstName", "Yash",
                    "lastName", "Kanetkar"),
            ImmutableMap.of("id", "author-2",
                    "firstName", "Mario",
                    "lastName", "Puzzo"),
            ImmutableMap.of("id", "author-3",
                    "firstName", "Arthur",
                    "lastName", "Doyle")
    );

    public DataFetcher getBookByIdDataFetcher() {
        return dataFetchingEnvironment-> {
            String bookId = dataFetchingEnvironment.getArgument("id");
            return books.stream()
                    .filter(book -> book.get("id").equals(bookId))
                    .findFirst()
                    .orElse(null);
        };
    }

    public DataFetcher getAuthorDataFetcher() {
        return dataFetchingEnvironment -> {
            // DFE.getSource() makes the previously fetched DataFetcher from Parent Field
            Map<String, String> book = dataFetchingEnvironment.getSource();

            String authorId = book.get("authorId");
            return authors.stream()
                    .filter(author -> author.get("id").equals(authorId))
                    .findFirst()
                    .orElse(null);
        };
    }
}
