package de.hanbei.rxsearch.searcher;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.SearchResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractSearcherTest {

    private static final String TEST_SEARCHER = "TestSearcher";
    private final SearchResult[] expectedSearchResults = {
            new SearchResult("url0", "title0", "icon0", TEST_SEARCHER),
            new SearchResult("url1", "title1", "icon1", TEST_SEARCHER),
            new SearchResult("url2", "title2", "icon2", TEST_SEARCHER),
    };
    private TestSearcher searcher;
    private ResponseParser responseParser;
    private AsyncHttpClient httpClient;

    @Before
    public void setUp() throws Exception {
        RequestUrlBuilder urlBuilder = mock(RequestUrlBuilder.class);
        when(urlBuilder.createRequestUrl(anyString())).thenReturn(mock(Request.class));

        responseParser = mock(ResponseParser.class);
        when(responseParser.toSearchResults(anyString())).thenReturn(Observable.from(expectedSearchResults));

        httpClient = mock(AsyncHttpClient.class, RETURNS_DEEP_STUBS);

        searcher = new TestSearcher(TEST_SEARCHER, urlBuilder, responseParser, httpClient);
    }

    @Test
    public void searcherReturnsCorrectResults() throws Exception {
        givenHttpClientSendsResponse(ok());

        Observable<SearchResult> observable = searcher.search("something");

        TestSubscriber<SearchResult> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertValueCount(expectedSearchResults.length);
        subscriber.assertCompleted();
        subscriber.assertValues(expectedSearchResults);
    }

    @Test
    public void searcherHandlesExceptionFromHttpClient() throws Exception {
        givenHttpClientThrows();

        Observable<SearchResult> observable = searcher.search("something");

        TestSubscriber<SearchResult> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertCompleted();
    }

    @Test
    public void searcherHandlesExceptionFromResponseParser() throws Exception {
        givenHttpClientSendsResponse(ok());
        givenResponseParserSendsErrorObservable();

        Observable<SearchResult> observable = searcher.search("something");
        TestSubscriber<SearchResult> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
    }

    @Test
    public void searcherHandlesExceptionFromOnCompleted() throws Exception {
        givenHttpClientSendsResponse(badRequest());
        givenResponseParserSendsErrorObservable();

        Observable<SearchResult> observable = searcher.search("something");
        TestSubscriber<SearchResult> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertNoErrors();
        subscriber.assertCompleted();
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
        when(responseParser.toSearchResults(anyString())).thenReturn(Observable.error(new RuntimeException("response parser error")));
    }


    private void givenHttpClientSendsResponse(Response response) throws IOException {
        when(httpClient.executeRequest(any(Request.class), any(AsyncCompletionHandler.class)))
                .thenAnswer((Answer<Void>) invocation -> {
                    ((AsyncCompletionHandler) invocation.getArguments()[1]).onCompleted(response);
                    return null;
                });
    }


    private void givenHttpClientThrows() throws IOException {
        when(httpClient.executeRequest(any(Request.class), any(AsyncCompletionHandler.class)))
                .thenAnswer((Answer<Void>) invocation -> {
                    ((AsyncCompletionHandler) invocation.getArguments()[1]).onThrowable(new RuntimeException("client error"));
                    return null;
                });
    }

    private static class TestSearcher extends AbstractSearcher {
        TestSearcher(String name, RequestUrlBuilder urlBuilder, ResponseParser responseParser, AsyncHttpClient asyncHttpClient) {
            super(name, urlBuilder, responseParser, asyncHttpClient);
        }
    }
}