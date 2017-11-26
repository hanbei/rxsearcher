package rxsearch.searcher;

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
    void testFromString() {
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
    void testDefaultPortHttp() {
        SearcherRequest request = SearcherRequest.from("http://www.example.com/").build();
        assertEquals(request.port(), 80);
    }

    @Test
    void testDefaultPortHttps() {
        SearcherRequest request = SearcherRequest.from("https://www.example.com/").build();
        assertEquals(request.port(), 443);
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
                .header("Accept", "*/*")
                .build();

        assertAll(
                () -> assertEquals(request.method(), POST),
                () -> assertEquals(request.protocol(), "http"),
                () -> assertEquals(request.host(), "www.example.com"),
                () -> assertEquals(request.port(), 9999),
                () -> assertEquals(request.path(), "/api/search"),
                () -> assertEquals(request.query("q"), newArrayList("test")),
                () -> assertEquals(request.query("sort"), newArrayList("bla")),
                () -> assertEquals(request.header("Accept"), newArrayList("*/*"))
        );
    }

    @Test
    void testBuilderRemovesEmptyPathElement() {
        SearcherRequest request = SearcherRequest.from()
                .path("api")
                .path("")
                .path("search")
                .build();

        assertEquals(request.path(), "/api/search");
    }

    @Test
    void testSignatureCalculator() {
        SearcherRequest request = SearcherRequest.from()
                .query("api", "test")
                .signatureCalculator(
                        (r, builder) ->
                                builder.header("api", r.query("api").stream().findFirst().get())
                )
                .build();

        assertEquals(request.header("api"), newArrayList("test"));
    }

}