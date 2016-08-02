package de.hanbei.rxsearch.searcher;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractSearcherTest {

    private static final String TEST_SEARCHER = "TestSearcher";
    public static final Query DUMMY_QUERY = new Query("something", "id");
    private final Offer[] expectedOffers = {
            Offer.builder().url("url0").title("title0").price(0.0, "USD").searcher("icon0").image(TEST_SEARCHER).build(),
            Offer.builder().url("url1").title("title1").price(0.0, "USD").searcher("icon1").image(TEST_SEARCHER).build(),
            Offer.builder().url("url2").title("title2").price(0.0, "USD").searcher("icon2").image(TEST_SEARCHER).build(),
    };

    private TestSearcher searcher;
    private ResponseParser responseParser;
    private AsyncHttpClient httpClient;

    @Before
    public void setUp() {
        RequestBuilder urlBuilder = mock(RequestBuilder.class);
        when(urlBuilder.createRequest(any(Query.class))).thenReturn(mock(Request.class));

        responseParser = mock(ResponseParser.class);
        when(responseParser.toSearchResults(any(Response.class))).thenReturn(Observable.from(expectedOffers));

        httpClient = mock(AsyncHttpClient.class, RETURNS_DEEP_STUBS);

        searcher = new TestSearcher(TEST_SEARCHER, urlBuilder, responseParser, httpClient);
    }

    @Test
    public void searcherReturnsCorrectResults() throws IOException {
        givenHttpClientSendsResponse(ok());

        Observable<Offer> observable = searcher.search(DUMMY_QUERY);

        TestSubscriber<Offer> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertValueCount(expectedOffers.length);
        subscriber.assertCompleted();
        subscriber.assertValues(expectedOffers);
    }

    @Test
    public void searcherEmitsExceptionFromHttpClient() throws IOException {
        givenHttpClientThrows();

        Observable<Offer> observable = searcher.search(DUMMY_QUERY);

        TestSubscriber<Offer> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertError(SearcherException.class);
    }

    @Test
    public void searcherEmitsExceptionFromResponseParser() throws IOException {
        givenHttpClientSendsResponse(ok());
        givenResponseParserSendsErrorObservable();

        Observable<Offer> observable = searcher.search(DUMMY_QUERY);
        TestSubscriber<Offer> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertError(SearcherException.class);
    }

    @Test
    public void searcherEmitsExceptionFromOnCompleted() throws IOException {
        givenHttpClientSendsResponse(badRequest());
        givenResponseParserSendsErrorObservable();

        Observable<Offer> observable = searcher.search(DUMMY_QUERY);
        TestSubscriber<Offer> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertError(SearcherException.class);
    }

    private Response badRequest() throws IOException {
        Response response = mock(Response.class);
        when(response.getResponseBody()).thenReturn("");
        when(response.getStatusCode()).thenReturn(400);
        return response;
    }

    private Response ok() throws IOException {
        Response response = mock(Response.class);
        when(response.getResponseBody()).thenReturn("");
        when(response.getStatusCode()).thenReturn(200);
        return response;
    }

    private void givenResponseParserSendsErrorObservable() {
        when(responseParser.toSearchResults(any(Response.class))).thenReturn(Observable.error(new SearcherException(new Query("t", "id"), "response parser error")));
    }

    @SuppressWarnings("unchecked")
    private void givenHttpClientSendsResponse(Response response) throws IOException {
        when(httpClient.executeRequest(any(Request.class), any(AsyncCompletionHandler.class)))
                .thenAnswer((Answer<Void>) invocation -> {
                    ((AsyncCompletionHandler) invocation.getArguments()[1]).onCompleted(response);
                    return null;
                });
    }

    @SuppressWarnings("unchecked")
    private void givenHttpClientThrows() throws IOException {
        when(httpClient.executeRequest(any(Request.class), any(AsyncCompletionHandler.class)))
                .thenAnswer((Answer<Void>) invocation -> {
                    ((AsyncCompletionHandler) invocation.getArguments()[1]).onThrowable(new RuntimeException("client error"));
                    return null;
                });
    }

    private static class TestSearcher extends AbstractSearcher {
        TestSearcher(String name, RequestBuilder urlBuilder, ResponseParser responseParser, AsyncHttpClient asyncHttpClient) {
            super(name, urlBuilder, responseParser, asyncHttpClient);
        }
    }
}