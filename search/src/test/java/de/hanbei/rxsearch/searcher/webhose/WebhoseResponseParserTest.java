package de.hanbei.rxsearch.searcher.webhose;

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
public class WebhoseResponseParserTest {
    private static final String WEBHOSE_SEARCHER = "Webhose";

    private WebhoseResponseParser responseParser;
    private Response response;

    @Before
    public void setUp() throws Exception {
        String stringResponse = Resources.toString(getResource("searcher/webhose/response_ok.json"), Charsets.UTF_8);
        response = mock(Response.class);
        when(response.getResponseBody(anyString())).thenReturn(stringResponse);

        responseParser = new WebhoseResponseParser(WEBHOSE_SEARCHER);
    }

    @Test
    public void testToSearchResults() throws Exception {
        Observable<Offer> observable = responseParser.toSearchResults(response);
        TestSubscriber<Offer> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertValueCount(5);
        subscriber.assertCompleted();
        subscriber.assertValues(
                Offer.builder().url("http://omgili.com/r/2wGaacqxApvQboY7on5a1l9L3GcBODsgg0QtL0JzN42dstbhyvXj.vUh0ZQBk_AHiGyX4VHlyLPUF4b0J7aB.QKYoctL7iu5EEkHG01WYcFpWdu4G.T4JDyQLThedIVpwCP0lH4OoVmw1lkkb3obciSMEYboRO146guVoB8GFVCv88vTLgjbEeeer4yNSeBv").title("LG Electronics And Korean Broadcasters Demonstrate Progress On ATSC 3.0 Standard").price(0.0, "USD").searcher(WEBHOSE_SEARCHER).image("").build(),
                Offer.builder().url("http://omgili.com/r/jHIAmI4hxg9HRCLv5qIdGxAoYKtkDs77Uv8F6sn1RmlIGaQyKbyUmI9L7g1w6qwdzyL_koQ6LHANOaMbKecXYM25CFRivn9hbUoSVG3BrEfF00awLNutPw--").title("Tren Ace Test Cyp help!!").price(0.0, "USD").searcher(WEBHOSE_SEARCHER).image("").build(),
                Offer.builder().url("http://omgili.com/r/jHIAmI4hxg9HRCLv5qIdGxAoYKtkDs77Uv8F6sn1RmlIGaQyKbyUmI9L7g1w6qwdzyL_koQ6LHANOaMbKecXYM25CFRivn9hbUoSVG3BrEfF00awLNutPw--").title("Tren Ace Test Cyp help!!2").price(0.0, "USD").searcher(WEBHOSE_SEARCHER).image("").build(),
                Offer.builder().url("http://omgili.com/r/jHIAmI4hxg9HRCLv5qIdGxAoYKtkDs77Uv8F6sn1RmlIGaQyKbyUmI9L7g1w6qwdzyL_koQ6LHANOaMbKecXYM25CFRivn9hbUoSVG3BrEfF00awLNutPw--").title("Tren Ace Test Cyp help!!3").price(0.0, "USD").searcher(WEBHOSE_SEARCHER).image("").build(),
                Offer.builder().url("http://omgili.com/r/jHIAmI4hxg9HRCLv5qIdGxAoYKtkDs77Uv8F6sn1RmlIGaQyKbyUmI9L7g1w6qwdzyL_koQ6LHANOaMbKecXYM25CFRivn9hbUoSVG3BrEcC2t45d5yq..HMAyfRc5vysuitTOyPJJ0-").title("").price(0.0, "USD").searcher(WEBHOSE_SEARCHER).image("").build());
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