package rxsearch.searcher;

import com.codahale.metrics.Counter;
import okhttp3.MediaType;
import rxsearch.model.Hit;
import rxsearch.model.Query;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractSearcherTest {

    private static final String TEST_SEARCHER = "TestSearcher";
    private static final Query DUMMY_QUERY = Query.builder().keywords("something").requestId("id").country("de").build();
    private final Hit[] expectedHits = {
            Hit.builder().url("url0").title("title0").searcher("icon0").image(TEST_SEARCHER).build(),
            Hit.builder().url("url1").title("title1").searcher("icon1").image(TEST_SEARCHER).build(),
            Hit.builder().url("url2").title("title2").searcher("icon2").image(TEST_SEARCHER).build(),
    };

    private TestSearcher searcher;
    private ResponseParser responseParser;
    private OkHttpClient httpClient;

    @Before
    public void setUp() {
        RequestBuilder urlBuilder = mock(RequestBuilder.class);
        when(urlBuilder.createRequest(any(Query.class))).thenReturn(mock(Request.class));

        responseParser = mock(ResponseParser.class);
        when(responseParser.toSearchResults(any(SearcherResponse.class))).thenReturn(Observable.fromArray(expectedHits));

        httpClient = mock(OkHttpClient.class, RETURNS_DEEP_STUBS);

        searcher = new TestSearcher(TEST_SEARCHER, urlBuilder, responseParser, httpClient);
    }

    @After
    public void resetMetric() {
        searcher.getMetricRegistry().getCounters().values().forEach(c -> c.dec(c.getCount()));
    }

    @Test
    public void searcherReturnsCorrectResults() throws IOException {
        givenHttpClientSendsResponse(ok());

        Observable<Hit> observable = searcher.search(DUMMY_QUERY);

        TestObserver<Hit> subscriber = new TestObserver<>();
        observable.subscribe(subscriber);
        subscriber.assertValueCount(expectedHits.length);
        subscriber.assertComplete();
        subscriber.assertValues(expectedHits);

        assertMetricSuccess();
    }

    @Test
    public void searcherEmitsExceptionFromHttpClient() throws IOException {
        givenHttpClientThrows();

        Observable<Hit> observable = searcher.search(DUMMY_QUERY);

        TestObserver<Hit> subscriber = new TestObserver<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertError(SearcherException.class);

        assertMetricError();
    }

    @Test
    public void searcherEmitsExceptionFromResponseParser() throws IOException {
        givenHttpClientSendsResponse(ok());
        givenResponseParserSendsErrorObservable();

        Observable<Hit> observable = searcher.search(DUMMY_QUERY);
        TestObserver<Hit> subscriber = new TestObserver<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertError(SearcherException.class);

        assertMetricError();
    }

    @Test
    public void searcherEmitsExceptionFromOnCompleted() throws IOException {
        givenHttpClientSendsResponse(badRequest());
        givenResponseParserSendsErrorObservable();

        Observable<Hit> observable = searcher.search(DUMMY_QUERY);
        TestObserver<Hit> subscriber = new TestObserver<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertError(SearcherException.class);

        assertMetricError();
    }

    private Response badRequest() throws IOException {
        Response response = mock(Response.class, RETURNS_DEEP_STUBS);
        when(response.body().bytes()).thenReturn("".getBytes());
        when(response.code()).thenReturn(400);
        when(response.body().contentType()).thenReturn(MediaType.parse("text/xml"));
        when(response.body().contentType()).thenReturn(MediaType.parse("text/xml"));
        return response;
    }

    private Response ok() throws IOException {
        Response response = mock(Response.class, RETURNS_DEEP_STUBS);
        when(response.body().bytes()).thenReturn("".getBytes());
        when(response.code()).thenReturn(200);
        when(response.body().contentType()).thenReturn(MediaType.parse("text/xml"));
        when(response.isSuccessful()).thenReturn(true);
        return response;
    }

    private void assertMetricError() {
        Counter counter = searcher.getMetricRegistry().getCounters().get("searcher.de." + TEST_SEARCHER + ".error");
        assertThat(counter.getCount(), is(1L));
    }

    private void assertMetricSuccess() {
        Counter counter = searcher.getMetricRegistry().getCounters().get("searcher.de." + TEST_SEARCHER + ".success");
        assertThat(counter.getCount(), is(1L));
    }

    private void givenResponseParserSendsErrorObservable() {
        when(responseParser.toSearchResults(any(SearcherResponse.class))).thenReturn(Observable.error(new SearcherException("response parser error").query(DUMMY_QUERY)));
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