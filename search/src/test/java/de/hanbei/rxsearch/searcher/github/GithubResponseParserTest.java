package de.hanbei.rxsearch.searcher.github;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.Offer;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GithubResponseParserTest {
    private static final String GITHUB_SEARCHER = "GithubSearcher";
    private static final String USD = "USD";

    private GithubResponseParser responseParser;
    private Response response;

    @Before
    public void setUp() throws IOException {

        String stringResponse = Resources.toString(getResource("searcher/github/response_ok.json"), Charsets.UTF_8);
        response = mock(Response.class);
        when(response.getResponseBody(anyString())).thenReturn(stringResponse);
        responseParser = new GithubResponseParser(GITHUB_SEARCHER);
    }

    @Test
    public void toSearchResultReturnsAllItems() {
        Observable<Offer> observable = responseParser.toSearchResults(response);
        TestObserver<Offer> subscriber = new TestObserver<>();
        observable.subscribe(subscriber);
        subscriber.assertValueCount(4);
        subscriber.assertComplete();
        subscriber.assertValues(Offer.builder().url("https://github.com/jquery/jquery/blob/055cb7534e2dcf7ee8ad145be83cb2d74b5331c7/test/localfile.html").title("localfile.html").price(0.0, USD).searcher(GITHUB_SEARCHER).image("").build(),
                Offer.builder().url("https://github.com/jquery/jquery/blob/055cb7534e2dcf7ee8ad145be83cb2d74b5331c7/src/attributes/classes.js").title("classes.js").price(0.0, USD).searcher(GITHUB_SEARCHER).image("").build(),
                Offer.builder().url("https://github.com/jquery/jquery/blob/44cb97e0cfc8d3e62bef7c621bfeba6fe4f65d7c/test/unit/attributes.js").title("attributes.js").price(0.0, USD).searcher(GITHUB_SEARCHER).image("").build(),
                Offer.builder().url("https://github.com/jquery/jquery/blob/755e7ccf018eb150eddefe78063a9ec58b3229e3/test/unit/effects.js").title("effects.js").price(0.0, USD).searcher(GITHUB_SEARCHER).image("").build());
    }

    @Test
    public void brokenJsonReturnsErrorObservable() throws IOException {
        when(response.getResponseBody(anyString())).thenReturn("{");

        Observable<Offer> observable = responseParser.toSearchResults(response);

        TestObserver<Offer> subscriber = new TestObserver<>();
        observable.subscribe(subscriber);
        subscriber.assertError(JsonParseException.class);
    }

    @Test
    public void correctButEmptyJsonReturnsEmptyObservable() throws IOException {
        when(response.getResponseBody(anyString())).thenReturn("{}");

        Observable<Offer> observable = responseParser.toSearchResults(response);

        TestObserver<Offer> subscriber = new TestObserver<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertNoErrors();
    }
}