package rxsearch.searcher;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static rxsearch.searcher.Method.GET;
import static rxsearch.searcher.Method.POST;

class SearcherRequestTest {

    @Test
    void testFromString() throws MalformedURLException {
        SearcherRequest request = SearcherRequest.from("http://www.example.com:8080/test/de?q=q&a=b").build();

        assertAll(
                () -> assertEquals(request.method(), GET),
                () -> assertEquals(request.protocol(), "http"),
                () -> assertEquals(request.host(), "www.example.com"),
                () -> assertEquals(request.port(), 8080),
                () -> assertEquals(request.path(), "/test/de"),
                () -> assertEquals(request.query("q"), newArrayList("q")),
                () -> assertEquals(request.query("a"), newArrayList("b"))
        );
    }

    @Test
    void testFromEmpty() throws MalformedURLException {
        SearcherRequest request = SearcherRequest.from().build();

        assertAll(
                () -> assertEquals(request.method(), GET),
                () -> assertEquals(request.protocol(), "http"),
                () -> assertEquals(request.host(), "localhost"),
                () -> assertEquals(request.port(), 80),
                () -> assertEquals(request.path(), "/"),
                () -> assertTrue(request.query().isEmpty())
        );
    }

    @Test
    void testBuilder() throws MalformedURLException {
        SearcherRequest request = SearcherRequest.from()
                .method(POST)
                .scheme("http")
                .host("www.example.com")
                .port(9999)
                .path("api")
                .path("search")
                .query("q", "test")
                .query("sort", "bla")
                .build();

        assertAll(
                () -> assertEquals(request.method(), POST),
                () -> assertEquals(request.protocol(), "http"),
                () -> assertEquals(request.host(), "www.example.com"),
                () -> assertEquals(request.port(), 9999),
                () -> assertEquals(request.path(), "/api/search"),
                () -> assertEquals(request.query("q"), newArrayList("test")),
                () -> assertEquals(request.query("sort"), newArrayList("bla"))
        );
    }

    @Test
    void testBuilderRemovesEmptyPathElement() throws MalformedURLException {
        SearcherRequest request = SearcherRequest.from()
                .method(POST)
                .scheme("http")
                .host("www.example.com")
                .port(9999)
                .path("api")
                .path("")
                .path("search")
                .query("q", "test")
                .query("sort", "bla")
                .build();

        assertAll(
                () -> assertEquals(request.method(), POST),
                () -> assertEquals(request.protocol(), "http"),
                () -> assertEquals(request.host(), "www.example.com"),
                () -> assertEquals(request.port(), 9999),
                () -> assertEquals(request.path(), "/api/search"),
                () -> assertEquals(request.query("q"), newArrayList("test")),
                () -> assertEquals(request.query("sort"), newArrayList("bla"))
        );
    }

}