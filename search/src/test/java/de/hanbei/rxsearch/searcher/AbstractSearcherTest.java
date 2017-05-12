package de.hanbei.rxsearch.searcher;

import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.model.User;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
public class AbstractSearcherTest {

    private static final String TEST_SEARCHER = "TestSearcher";
    private static final Query DUMMY_QUERY = Query.builder().keywords("something").requestId("id").country("de").user(User.getDefaultUser()).build();
    private final Offer[] expectedOffers = {
            Offer.builder().url("url0").title("title0").price(0.0, "USD").searcher("icon0").image(TEST_SEARCHER).build(),
            Offer.builder().url("url1").title("title1").price(0.0, "USD").searcher("icon1").image(TEST_SEARCHER).build(),
            Offer.builder().url("url2").title("title2").price(0.0, "USD").searcher("icon2").image(TEST_SEARCHER).build(),
    };

    private TestSearcher searcher;
    private ResponseParser responseParser;
    private OkHttpClient httpClient;

    @Before
    public void setUp() {
        RequestBuilder urlBuilder = mock(RequestBuilder.class);
        when(urlBuilder.createRequest(any(Query.class))).thenReturn(mock(Request.class));

        responseParser = mock(ResponseParser.class);
        when(responseParser.toSearchResults(any(Response.class))).thenReturn(Observable.fromArray(expectedOffers));

        httpClient = mock(OkHttpClient.class, RETURNS_DEEP_STUBS);

        searcher = new TestSearcher(TEST_SEARCHER, urlBuilder, responseParser, httpClient);
    }

    @Test
    public void searcherReturnsCorrectResults() throws IOException {
        givenHttpClientSendsResponse(ok());

        Observable<Offer> observable = searcher.search(DUMMY_QUERY);

        TestObserver<Offer> subscriber = new TestObserver<>();
        observable.subscribe(subscriber);
        subscriber.assertValueCount(expectedOffers.length);
        subscriber.assertComplete();
        subscriber.assertValues(expectedOffers);
    }

    @Test
    public void searcherEmitsExceptionFromHttpClient() throws IOException {
        givenHttpClientThrows();

        Observable<Offer> observable = searcher.search(DUMMY_QUERY);

        TestObserver<Offer> subscriber = new TestObserver<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertError(SearcherException.class);
    }

    @Test
    public void searcherEmitsExceptionFromResponseParser() throws IOException {
        givenHttpClientSendsResponse(ok());
        givenResponseParserSendsErrorObservable();

        Observable<Offer> observable = searcher.search(DUMMY_QUERY);
        TestObserver<Offer> subscriber = new TestObserver<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertError(SearcherException.class);
    }

    @Test
    public void searcherEmitsExceptionFromOnCompleted() throws IOException {
        givenHttpClientSendsResponse(badRequest());
        givenResponseParserSendsErrorObservable();

        Observable<Offer> observable = searcher.search(DUMMY_QUERY);
        TestObserver<Offer> subscriber = new TestObserver<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertError(SearcherException.class);
    }

    private Response badRequest() throws IOException {
        Response response = mock(Response.class, RETURNS_DEEP_STUBS);
        when(response.body().string()).thenReturn("");
        when(response.code()).thenReturn(400);
        return response;
    }

    private Response ok() throws IOException {
        Response response = mock(Response.class, RETURNS_DEEP_STUBS);
        when(response.body().string()).thenReturn("");
        when(response.code()).thenReturn(200);
        when(response.isSuccessful()).thenReturn(true);
        return response;
    }

    private void givenResponseParserSendsErrorObservable() {
        when(responseParser.toSearchResults(any(Response.class))).thenReturn(Observable.error(new SearcherException("response parser error").query(DUMMY_QUERY)));
    }

    @SuppressWarnings("unchecked")
    private void givenHttpClientSendsResponse(Response response) {
        Call call = mock(Call.class);
        when(httpClient.newCall(any(Request.class))).thenReturn(call);

        doAnswer(invocation -> {
            ((Callback) invocation.getArgument(0)).onResponse(null, response);
            return null;
        }).when(call).enqueue(any(Callback.class));
    }

    @SuppressWarnings("unchecked")
    private void givenHttpClientThrows() {
        Call call = mock(Call.class);
        when(httpClient.newCall(any(Request.class))).thenReturn(call);

        doAnswer(invocation -> {
            ((Callback) invocation.getArgument(0)).onFailure(null, new IOException("client error"));
            return null;
        }).when(call).enqueue(any(Callback.class));
    }

    private static class TestSearcher extends AbstractSearcher {
        TestSearcher(String name, RequestBuilder urlBuilder, ResponseParser responseParser, OkHttpClient asyncHttpClient) {
            super(name, urlBuilder, responseParser, asyncHttpClient);
        }
    }
}