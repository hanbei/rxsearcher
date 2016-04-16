package de.hanbei.rxsearch.searcher.duckduckgo;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.SearchResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import static com.google.common.io.Resources.getResource;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by hanbei on 4/16/16.
 */
public class DuckDuckGoSearcherTest {
    private DuckDuckGoSearcher searcher;

    @Before
    public void setUp() throws Exception {
        Response response = mock(Response.class);
        when(response.getResponseBody()).thenReturn(Resources.toString(getResource("searcher/duckduckgo/response_ok.json"), Charsets.UTF_8));

        AsyncHttpClient httpClient = mock(AsyncHttpClient.class, RETURNS_DEEP_STUBS);
        when(httpClient.prepareGet(anyString()).execute(any(AsyncCompletionHandler.class)))
                .thenAnswer(invocation -> {
                    ((AsyncCompletionHandler) invocation.getArguments()[0]).onCompleted(response);
                    return null;
                });
        searcher = new DuckDuckGoSearcher("DuckDuckGoSearcher", httpClient);
    }


    @Test
    public void searcherReturnsCorrectResults() throws Exception {
        Observable<SearchResult> observable = searcher.search("something");
        TestSubscriber<SearchResult> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertValueCount(11);
        subscriber.assertCompleted();
    }

}