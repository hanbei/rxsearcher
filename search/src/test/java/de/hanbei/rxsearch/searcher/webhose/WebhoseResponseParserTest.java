package de.hanbei.rxsearch.searcher.webhose;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import de.hanbei.rxsearch.model.SearchResult;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import static com.google.common.io.Resources.getResource;

/**
 * Created by fschulz on 22.04.2016.
 */
public class WebhoseResponseParserTest {
    private static final String WEBHOSE_SEARCHER = "Webhose";

    private String s;
    private WebhoseResponseParser responseParser;

    @Before
    public void setUp() throws Exception {
        s = Resources.toString(getResource("searcher/webhose/response_ok.json"), Charsets.UTF_8);
        responseParser = new WebhoseResponseParser(WEBHOSE_SEARCHER);
    }

    @Test
    public void testToSearchResults() throws Exception {
        Observable<SearchResult> observable = responseParser.toSearchResults(s);
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

    @Test
    public void brokenJsonReturnsErrorObservable() {
        Observable<SearchResult> observable = responseParser.toSearchResults("{");
        TestSubscriber<SearchResult> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertError(JsonParseException.class);
    }

    @Test
    public void correctButEmptyJsonReturnsEmptyObservable() {
        Observable<SearchResult> observable = responseParser.toSearchResults("{}");
        TestSubscriber<SearchResult> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertNoErrors();
    }
}