package de.hanbei.rxsearch.searcher.github;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.SearchResult;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by fschulz on 22.04.2016.
 */
public class GithubResponseParserTest {
    private static final String GITHUB_SEARCHER = "GithubSearcher";

    private GithubResponseParser responseParser;
    private Response response;

    @Before
    public void setUp() throws Exception {
        String stringResponse = Resources.toString(getResource("searcher/github/response_ok.json"), Charsets.UTF_8);
        response = mock(Response.class);
        when(response.getResponseBody(anyString())).thenReturn(stringResponse);
        responseParser = new GithubResponseParser(GITHUB_SEARCHER);
    }

    @Test
    public void toSearchResultReturnsAllItems() throws Exception {
        Observable<SearchResult> observable = responseParser.toSearchResults(response);
        TestSubscriber<SearchResult> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertValueCount(4);
        subscriber.assertCompleted();
        subscriber.assertValues(new SearchResult("https://github.com/jquery/jquery/blob/055cb7534e2dcf7ee8ad145be83cb2d74b5331c7/test/localfile.html", "localfile.html", GITHUB_SEARCHER),
                new SearchResult("https://github.com/jquery/jquery/blob/055cb7534e2dcf7ee8ad145be83cb2d74b5331c7/src/attributes/classes.js", "classes.js", GITHUB_SEARCHER),
                new SearchResult("https://github.com/jquery/jquery/blob/44cb97e0cfc8d3e62bef7c621bfeba6fe4f65d7c/test/unit/attributes.js", "attributes.js", GITHUB_SEARCHER),
                new SearchResult("https://github.com/jquery/jquery/blob/755e7ccf018eb150eddefe78063a9ec58b3229e3/test/unit/effects.js", "effects.js", GITHUB_SEARCHER));
    }

    @Test
    public void brokenJsonReturnsErrorObservable() throws IOException {
        when(response.getResponseBody(anyString())).thenReturn("{");

        Observable<SearchResult> observable = responseParser.toSearchResults(response);

        TestSubscriber<SearchResult> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertError(JsonParseException.class);
    }

    @Test
    public void correctButEmptyJsonReturnsEmptyObservable() throws IOException {
        when(response.getResponseBody(anyString())).thenReturn("{}");

        Observable<SearchResult> observable = responseParser.toSearchResults(response);

        TestSubscriber<SearchResult> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertNoErrors();
    }
}