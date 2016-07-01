package de.hanbei.rxsearch.searcher.duckduckgo;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.Offer;
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
public class DuckDuckGoResponseParserTest {

    private static final String DUCK_DUCK_GO_SEARCHER = "DuckDuckGoSearcher";
    private Response response;
    private DuckDuckGoResponseParser responseParser;

    @Before
    public void setUp() throws Exception {
        response = mock(Response.class);
        String stringResponse = Resources.toString(getResource("searcher/duckduckgo/response_ok.json"), Charsets.UTF_8);
        when(response.getResponseBody(anyString())).thenReturn(stringResponse);
        responseParser = new DuckDuckGoResponseParser(DUCK_DUCK_GO_SEARCHER);
    }

    @Test
    public void correctResponseIsParseable() throws Exception {
        Observable<Offer> observable = responseParser.toSearchResults(response);
        TestSubscriber<Offer> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertCompleted();
        subscriber.assertValueCount(8);
        subscriber.assertValues(
                Offer.build("https://duckduckgo.com/Google", "Google An American multinational technology company specializing in Internet-related services and...", DUCK_DUCK_GO_SEARCHER, "https://duckduckgo.com/i/8f85c93f.png"),
                Offer.build("https://duckduckgo.com/Google_Search", "Google SearchA web search engine owned by Google Inc. It is the most-used search engine on the World Wide Web...", DUCK_DUCK_GO_SEARCHER, "https://duckduckgo.com/i/8f85c93f.png"),
                Offer.build("https://duckduckgo.com/Google_(verb)", "Google (verb)Using the Google search engine to obtain information on something or somebody on the World Wide Web.", DUCK_DUCK_GO_SEARCHER, ""),
                Offer.build("https://duckduckgo.com/Google.org", "Google.org, founded in October 2005, is the charitable arm of Google, an Internet search engine...", DUCK_DUCK_GO_SEARCHER, "https://duckduckgo.com/i/a707b7ea.png"),
                Offer.build("https://duckduckgo.com/Googal%2C_Devadurga", "Googal, DevadurgaA village in the Devadurga taluk of Raichur district in Karnataka state, India.", DUCK_DUCK_GO_SEARCHER, ""),
                Offer.build("https://duckduckgo.com/Goggles", "Goggles or safety glasses are forms of protective eyewear that usually enclose or protect the...", DUCK_DUCK_GO_SEARCHER, "https://duckduckgo.com/i/82de7b2a.jpg"),
                Offer.build("https://duckduckgo.com/Goggles!", "Goggles! Goggles! is a 1969 children's picture book by American author and illustrator Ezra Jack Keats...", DUCK_DUCK_GO_SEARCHER, "https://duckduckgo.com/i/d291493f.jpeg"),
                Offer.build("https://duckduckgo.com/Googly_eyes", "Googly eyesGoogly eyes, or jiggly eyes are small plastic craft supplies used to imitate eyeballs.", DUCK_DUCK_GO_SEARCHER, "https://duckduckgo.com/i/cf399a0e.jpg"));

    }

    @Test
    public void brokenJsonReturnsErrorObservable() throws IOException {
        when(response.getResponseBody(anyString())).thenReturn("{");

        Observable<Offer> observable = responseParser.toSearchResults(response);

        TestSubscriber<Offer> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertError(JsonParseException.class);
    }

    @Test
    public void correctButEmptyJsonReturnsEmptyObservable() throws IOException {
        when(response.getResponseBody(anyString())).thenReturn("{}");

        Observable<Offer> observable = responseParser.toSearchResults(response);

        TestSubscriber<Offer> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertNoErrors();
    }
}