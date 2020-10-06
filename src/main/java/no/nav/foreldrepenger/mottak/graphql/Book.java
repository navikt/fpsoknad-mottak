package no.nav.foreldrepenger.mottak.graphql;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Book {
    private final String id;
    private final String name;
    private final String pageCount;
    private final Author author;

    @JsonCreator
    public Book(@JsonProperty("id") String id, @JsonProperty("name") String name, @JsonProperty("pageCount") String pageCount,
            @JsonProperty("author") Author author) {
        this.id = id;
        this.name = name;
        this.pageCount = pageCount;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPageCount() {
        return pageCount;
    }

    public Author getAuthor() {
        return author;
    }
}
