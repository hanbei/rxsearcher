package rxsearch.searcher.duckduckgo;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import rxsearch.model.Hit;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import okhttp3.Response;
import org.junit.Before;
import org.junit.Test;
import rxsearch.searcher.SearcherResponse;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DuckDuckGoResponseParserTest {

    private static final String DUCK_DUCK_GO_SEARCHER = "DuckDuckGoSearcher";
    private static final String USD = "USD";
    private SearcherResponse response;
    private DuckDuckGoResponseParser responseParser;

    @Before
    public void setUp() throws IOException {
        response = mock(SearcherResponse.class, RETURNS_DEEP_STUBS);
        String stringResponse = Resources.toString(getResource("searcher/duckduckgo/response_ok.json"), Charsets.UTF_8);
        when(response.content().array()).thenReturn(stringResponse.getBytes());
        responseParser = new DuckDuckGoResponseParser(DUCK_DUCK_GO_SEARCHER);
    }

    @Test
    public void correctResponseIsParseable() {
        Observable<Hit> observable = responseParser.toSearchResults(response);

        TestObserver<Hit> subscriber = new TestObserver<>();

        observable.subscribe(subscriber);
        subscriber.assertComplete();
        subscriber.assertValueCount(8);
        subscriber.assertValues(
                Hit.builder().url("https://duckduckgo.com/Google").title("Google An American multinational technology company specializing in Internet-related services and...").searcher(DUCK_DUCK_GO_SEARCHER).image("https://duckduckgo.com/i/8f85c93f.png").build(),
                Hit.builder().url("https://duckduckgo.com/Google_Search").title("Google SearchA web search engine owned by Google Inc. It is the most-used search engine on the World Wide Web...").searcher(DUCK_DUCK_GO_SEARCHER).image("https://duckduckgo.com/i/8f85c93f.png").build(),
                Hit.builder().url("https://duckduckgo.com/Google_(verb)").title("Google (verb)Using the Google search engine to obtain information on something or somebody on the World Wide Web.").searcher(DUCK_DUCK_GO_SEARCHER).image("").build(),
                Hit.builder().url("https://duckduckgo.com/Google.org").title("Google.org, founded in October 2005, is the charitable arm of Google, an Internet search engine...").searcher(DUCK_DUCK_GO_SEARCHER).image("https://duckduckgo.com/i/a707b7ea.png").build(),
                Hit.builder().url("https://duckduckgo.com/Googal%2C_Devadurga").title("Googal, DevadurgaA village in the Devadurga taluk of Raichur district in Karnataka state, India.").searcher(DUCK_DUCK_GO_SEARCHER).image("").build(),
                Hit.builder().url("https://duckduckgo.com/Goggles").title("Goggles or safety glasses are forms of protective eyewear that usually enclose or protect the...").searcher(DUCK_DUCK_GO_SEARCHER).image("https://duckduckgo.com/i/82de7b2a.jpg").build(),
                Hit.builder().url("https://duckduckgo.com/Goggles!").title("Goggles! Goggles! is a 1969 children's picture book by American author and illustrator Ezra Jack Keats...").searcher(DUCK_DUCK_GO_SEARCHER).image("https://duckduckgo.com/i/d291493f.jpeg").build(),
                Hit.builder().url("https://duckduckgo.com/Googly_eyes").title("Googly eyesGoogly eyes, or jiggly eyes are small plastic craft supplies used to imitate eyeballs.").searcher(DUCK_DUCK_GO_SEARCHER).image("https://duckduckgo.com/i/cf399a0e.jpg").build());

    }

    @Test
    public void brokenJsonReturnsErrorObservable() throws IOException {
        when(response.content().array()).thenReturn("{".getBytes());

        Observable<Hit> observable = responseParser.toSearchResults(response);

        TestObserver<Hit> subscriber = new TestObserver<>();
        observable.subscribe(subscriber);
        subscriber.assertError(JsonParseException.class);
    }

    @Test
    public void correctButEmptyJsonReturnsEmptyObservable() throws IOException {
        when(response.content().array()).thenReturn("{}".getBytes());

        Observable<Hit> observable = responseParser.toSearchResults(response);

        TestObserver<Hit> subscriber = new TestObserver<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertNoErrors();
    }
}