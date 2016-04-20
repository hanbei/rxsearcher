package de.hanbei.rxsearch.searcher.webhose;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.SearchResult;
import de.hanbei.rxsearch.searcher.github.GithubSearcher;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by hanbei on 4/17/16.
 */
public class WebhoseSearcherTest {
    private static final String WEBHOSE_SEARCHER = "Webhose";
    private WebhoseSearcher searcher;

    @Before
    public void setUp() throws Exception {
        AsyncHttpClient httpClient = mock(AsyncHttpClient.class, RETURNS_DEEP_STUBS);
        httpClient = setupResponse(httpClient);

        searcher = new WebhoseSearcher(WEBHOSE_SEARCHER, "key", httpClient);
    }

    private AsyncHttpClient setupResponse(AsyncHttpClient httpClient) throws IOException {
        Response response = mock(Response.class);
        when(response.getResponseBody()).thenReturn(Resources.toString(getResource("searcher/webhose/response_ok.json"), Charsets.UTF_8));

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
        subscriber.assertValueCount(5);
        subscriber.assertCompleted();
        subscriber.assertValues(
                new SearchResult("http://omgili.com/r/2wGaacqxApvQboY7on5a1l9L3GcBODsgg0QtL0JzN42dstbhyvXj.vUh0ZQBk_AHiGyX4VHlyLPUF4b0J7aB.QKYoctL7iu5EEkHG01WYcFpWdu4G.T4JDyQLThedIVpwCP0lH4OoVmw1lkkb3obciSMEYboRO146guVoB8GFVCv88vTLgjbEeeer4yNSeBv", "LG Electronics And Korean Broadcasters Demonstrate Progress On ATSC 3.0 Standard", WEBHOSE_SEARCHER),
                new SearchResult("http://omgili.com/r/jHIAmI4hxg9HRCLv5qIdGxAoYKtkDs77Uv8F6sn1RmlIGaQyKbyUmI9L7g1w6qwdzyL_koQ6LHANOaMbKecXYM25CFRivn9hbUoSVG3BrEfF00awLNutPw--", "Tren Ace Test Cyp help!!", WEBHOSE_SEARCHER),
                new SearchResult("http://omgili.com/r/jHIAmI4hxg9HRCLv5qIdGxAoYKtkDs77Uv8F6sn1RmlIGaQyKbyUmI9L7g1w6qwdzyL_koQ6LHANOaMbKecXYM25CFRivn9hbUoSVG3BrEfF00awLNutPw--", "Tren Ace Test Cyp help!!2", WEBHOSE_SEARCHER),
                new SearchResult("http://omgili.com/r/jHIAmI4hxg9HRCLv5qIdGxAoYKtkDs77Uv8F6sn1RmlIGaQyKbyUmI9L7g1w6qwdzyL_koQ6LHANOaMbKecXYM25CFRivn9hbUoSVG3BrEfF00awLNutPw--", "Tren Ace Test Cyp help!!3", WEBHOSE_SEARCHER),
                new SearchResult("http://omgili.com/r/jHIAmI4hxg9HRCLv5qIdGxAoYKtkDs77Uv8F6sn1RmlIGaQyKbyUmI9L7g1w6qwdzyL_koQ6LHANOaMbKecXYM25CFRivn9hbUoSVG3BrEcC2t45d5yq..HMAyfRc5vysuitTOyPJJ0-", "", WEBHOSE_SEARCHER));
    }

}