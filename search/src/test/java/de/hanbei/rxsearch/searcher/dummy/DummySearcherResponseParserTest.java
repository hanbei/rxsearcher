package de.hanbei.rxsearch.searcher.dummy;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.SearchResult;
import de.hanbei.rxsearch.searcher.webhose.WebhoseResponseParser;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by hanbei on 5/16/16.
 */
public class DummySearcherResponseParserTest {

    private Response response;
    private DummySearcherResponseParser responseParser;

    @Before
    public void setUp() throws Exception {
        String stringResponse = Resources.toString(getResource("searcher/dummy/response_ok.json"), Charsets.UTF_8);
        response = mock(Response.class);
        when(response.getResponseBody(anyString())).thenReturn(stringResponse);

        responseParser = new DummySearcherResponseParser("source");
    }

    @Test
    public void testToSearchResults() throws Exception {
        Observable<SearchResult> observable = responseParser.toSearchResults(response);
        TestSubscriber<SearchResult> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertValueCount(3);
        subscriber.assertCompleted();
        subscriber.assertValues(
                new SearchResult("http://ecs-de.kelkoo.de/ctl/go/offersearchGo?.ts=1458822797812&.sig=s8k9hmFVqFmDUlE.Q6FNaD_b5oo-&catId=100091613&localCatId=100091613&comId=11438223&offerId=de4bae6573afc390a71f43be7ff82bfd&searchId=null&affiliationId=96947156&country=de&wait=true&contextLevel=2&service=11", "Samsung CLX-3305 - Samsung Partner", "source", "http://r.kelkoo.com/r/de/11438223/100091613/90/90/https%3A%2F%2Fmedia.c-nw.de%2Fmedia%2Fextendware%2Fewimageopt%2Fmedia%2Finline%2F16%2F7%2Fsamsung-clx-3305-a57.jpg/q.MEOiH2m9W0VYhpKnp1QgPvozBPJOZbu11YXlM3JiQ-"),
                new SearchResult("http://ecs-de.kelkoo.de/ctl/go/offersearchGo?.ts=1458822797812&.sig=FyMcaUGUBHhyBYOkA23gQN5G8PQ-&catId=100005613&localCatId=100005613&comId=11559213&offerId=9ea8701ce37856cd21d61dfaad5e2cf8&searchId=null&affiliationId=96947156&country=de&wait=true&contextLevel=2&service=11", "Samsung 2 Samsung Toner SCX-P6320A/ELS schwarz", "source", "http://r.kelkoo.com/r/de/11559213/100005613/90/90/http%3A%2F%2Fwww.s297775433.online.de%2Fherstellerbilder%2Flt1066_2.gif/Uo45vtgYncsdDgJrqw6OEaiHe4EwS5Y5Xz9CsN32ZWg-"),
                new SearchResult("http://ecs-de.kelkoo.de/ctl/go/offersearchGo?.ts=1458822797812&.sig=LUoIGCPYqrQkD6GWOc94YMGBl6g-&catId=100005613&localCatId=100005613&comId=8567623&offerId=0a2c1899e38d4a51de88881c45a73c63&searchId=null&affiliationId=96947156&country=de&wait=true&contextLevel=2&service=11", "Samsung 2 Samsung Toner SCX-P4216A/ELS schwarz", "source", "http://r.kelkoo.com/r/de/8567623/100005613/90/90/http%3A%2F%2Fwww.s297775433.online.de%2Fherstellerbilder%2Flt1150_2.gif/OT7Jl2MJ_W6FJKuA2VaDt8Vig.zRFUaUU9jWLmQLYEg-"));
    }

    @Test
    public void brokenJsonReturnsErrorObservable() throws IOException {
        when(response.getResponseBody(anyString())).thenReturn("{");

        Observable<SearchResult> observable = responseParser.toSearchResults(response);

        TestSubscriber<SearchResult> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertError(JsonParseException.class);
    }

    @Test
    public void correctButEmptyJsonReturnsEmptyObservable() throws IOException {
        when(response.getResponseBody(anyString())).thenReturn("{}");

        Observable<SearchResult> observable = responseParser.toSearchResults(response);
        TestSubscriber<SearchResult> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);
        subscriber.assertNoValues();
        subscriber.assertNoErrors();
    }

}