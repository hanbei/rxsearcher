package de.hanbei.rxsearch.searcher.github;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.SearchResult;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by hanbei on 4/17/16.
 */
public class GithubSearcherTest {
    private static final String GITHUB_SEARCHER = "GithubSearcher";
    private GithubSearcher searcher;

    @Before
    public void setUp() throws Exception {
        AsyncHttpClient httpClient = mock(AsyncHttpClient.class, RETURNS_DEEP_STUBS);
        httpClient = setupResponse(httpClient);

        searcher = new GithubSearcher(GITHUB_SEARCHER, "jquery/jquery", httpClient);
    }

    private AsyncHttpClient setupResponse(AsyncHttpClient httpClient) throws IOException {
        Response response = mock(Response.class);
        when(response.getResponseBody()).thenReturn(Resources.toString(getResource("searcher/github/response_ok.json"), Charsets.UTF_8));

        when(httpClient.prepareGet(anyString()).execute(any(AsyncCompletionHandler.class)))
                .thenAnswer(invocation -> {
                    ((AsyncCompletionHandler) invocation.getArguments()[0]).onCompleted(response);
                    return null;
                });
        return httpClient;
    }

    @Test
    public void searcherReturnsCorrectResults() throws Exception {
        Observable<SearchResult> observable = searcher.search("something");
        TestSubscriber<SearchResult> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertValueCount(4);
        subscriber.assertCompleted();
        subscriber.assertValues(new SearchResult("https://github.com/jquery/jquery/blob/055cb7534e2dcf7ee8ad145be83cb2d74b5331c7/test/localfile.html", "localfile.html", GITHUB_SEARCHER),
                new SearchResult("https://github.com/jquery/jquery/blob/055cb7534e2dcf7ee8ad145be83cb2d74b5331c7/src/attributes/classes.js", "classes.js", GITHUB_SEARCHER),
                new SearchResult("https://github.com/jquery/jquery/blob/44cb97e0cfc8d3e62bef7c621bfeba6fe4f65d7c/test/unit/attributes.js", "attributes.js", GITHUB_SEARCHER),
                new SearchResult("https://github.com/jquery/jquery/blob/755e7ccf018eb150eddefe78063a9ec58b3229e3/test/unit/effects.js", "effects.js", GITHUB_SEARCHER));
    }

}