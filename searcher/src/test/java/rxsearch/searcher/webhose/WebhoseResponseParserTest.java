package rxsearch.searcher.webhose;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;
import rxsearch.model.Hit;
import rxsearch.searcher.SearcherResponse;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebhoseResponseParserTest {
    private static final String WEBHOSE_SEARCHER = "Webhose";
    private static final String USD = "USD";

    private WebhoseResponseParser responseParser;
    private SearcherResponse response;

    @Before
    public void setUp() throws IOException {
        String stringResponse = Resources.toString(getResource("searcher/webhose/response_ok.json"), Charsets.UTF_8);
        response = mock(SearcherResponse.class, RETURNS_DEEP_STUBS);
        when(response.content().array()).thenReturn(stringResponse.getBytes());

        responseParser = new WebhoseResponseParser(WEBHOSE_SEARCHER);
    }

    @Test
    public void testToSearchResults() {
        Observable<Hit> observable = responseParser.toSearchResults(response);
        TestObserver<Hit> subscriber = new TestObserver<>();
        observable.subscribe(subscriber);
        subscriber.assertValueCount(5);
        subscriber.assertComplete();
        subscriber.assertValues(
                Hit.builder().url("http://omgili.com/r/1").title("LG Electronics And Korean Broadcasters Demonstrate Progress On ATSC 3.0 Standard").searcher(WEBHOSE_SEARCHER).build(),
                Hit.builder().url("http://omgili.com/r/3").title("Tren Ace Test Cyp help!!").searcher(WEBHOSE_SEARCHER).build(),
                Hit.builder().url("http://omgili.com/r/5").title("Tren Ace Test Cyp help!!2").searcher(WEBHOSE_SEARCHER).build(),
                Hit.builder().url("http://omgili.com/r/7").title("Tren Ace Test Cyp help!!3").searcher(WEBHOSE_SEARCHER).build(),
                Hit.builder().url("http://omgili.com/r/9").title("").searcher(WEBHOSE_SEARCHER).build());
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