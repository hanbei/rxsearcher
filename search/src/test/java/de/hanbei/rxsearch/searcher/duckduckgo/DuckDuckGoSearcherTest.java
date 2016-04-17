package de.hanbei.rxsearch.searcher.duckduckgo;

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
 * Created by hanbei on 4/16/16.
 */
public class DuckDuckGoSearcherTest {
    private DuckDuckGoSearcher searcher;
    private AsyncHttpClient httpClient;

    @Before
    public void setUp() throws Exception {
        httpClient = mock(AsyncHttpClient.class, RETURNS_DEEP_STUBS);
        httpClient = setupResponse(httpClient);

        searcher = new DuckDuckGoSearcher("DuckDuckGoSearcher", httpClient);
    }

    private AsyncHttpClient setupResponse(AsyncHttpClient httpClient) throws IOException {
        Response response = mock(Response.class);
        when(response.getResponseBody()).thenReturn(Resources.toString(getResource("searcher/duckduckgo/response_ok.json"), Charsets.UTF_8));


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
        subscriber.assertValueCount(8);
        subscriber.assertCompleted();
        subscriber.assertValues(
                new SearchResult("https://duckduckgo.com/Google", "Google An American multinational technology company specializing in Internet-related services and...", "https://duckduckgo.com/i/8f85c93f.png"),
                new SearchResult("https://duckduckgo.com/Google_Search", "Google SearchA web search engine owned by Google Inc. It is the most-used search engine on the World Wide Web...", "https://duckduckgo.com/i/8f85c93f.png"),
                new SearchResult("https://duckduckgo.com/Google_(verb)", "Google (verb)Using the Google search engine to obtain information on something or somebody on the World Wide Web."),
                new SearchResult("https://duckduckgo.com/Google.org", "Google.org, founded in October 2005, is the charitable arm of Google, an Internet search engine...", "https://duckduckgo.com/i/a707b7ea.png"),
                new SearchResult("https://duckduckgo.com/Googal%2C_Devadurga", "Googal, DevadurgaA village in the Devadurga taluk of Raichur district in Karnataka state, India."),
                new SearchResult("https://duckduckgo.com/Goggles", "Goggles or safety glasses are forms of protective eyewear that usually enclose or protect the...", "https://duckduckgo.com/i/82de7b2a.jpg"),
                new SearchResult("https://duckduckgo.com/Goggles!", "Goggles! Goggles! is a 1969 children's picture book by American author and illustrator Ezra Jack Keats...", "https://duckduckgo.com/i/d291493f.jpeg"),
                new SearchResult("https://duckduckgo.com/Googly_eyes", "Googly eyesGoogly eyes, or jiggly eyes are small plastic craft supplies used to imitate eyeballs.", "https://duckduckgo.com/i/cf399a0e.jpg"));
    }

}