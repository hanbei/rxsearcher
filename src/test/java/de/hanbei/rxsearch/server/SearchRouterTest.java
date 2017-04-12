package de.hanbei.rxsearch.server;

import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.model.User;
import de.hanbei.rxsearch.searcher.Searcher;
import io.reactivex.Observable;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SearchRouterTest {

    private static final String SEARCH_TERM = "search_term";
    private static final String ID = "id";
    private static final String DE = "de";
    private static final Query QUERY = Query.builder().keywords(SEARCH_TERM).requestId(ID).country(DE).user(new User("user_id","partner_id","partner_sub_id")).build();
    private SearchRouter router;
    private RoutingContext routingContext;

    private Searcher searcher1;
    private Searcher searcher2;

    private HttpServerResponse response;

    @Before
    public void setUp() {
        searcher1 = mock(Searcher.class);
        when(searcher1.getName()).thenReturn("searcher1");
        searcher2 = mock(Searcher.class);
        when(searcher2.getName()).thenReturn("searcher2");

        HttpServerRequest request = mock(HttpServerRequest.class);
        when(request.getHeader("X-Request-ID")).thenReturn(ID);

        response = mock(HttpServerResponse.class);
        when(response.putHeader(eq(HttpHeaders.CONTENT_TYPE), anyString())).thenReturn(response);

        routingContext = mock(RoutingContext.class);
        when(routingContext.request()).thenReturn(request);
        when(routingContext.response()).thenReturn(response);
        when(routingContext.getBodyAsJson()).thenReturn(new JsonObject(
                "{\"query\":{\n" +
                        "  \"keywords\":\"" + SEARCH_TERM + "\",\n" +
                        "  \"country\":\""+DE+"\"," +
                        "  \"user\":{" +
                        "     \"id\":\"user_id\","+
                        "     \"partnerId\":\"partner_id\","+
                        "     \"partnerSubId\":\"partner_sub_id\""+
                        "  }"+
                        "}}"));

        router = new SearchRouter(newArrayList(searcher1, searcher2), newArrayList(), mock(EventBus.class));
    }

    @Test
    public void whenSearcherRespondRendersCorrectJson() {
        when(searcher1.search(QUERY)).thenReturn(Observable.just(Offer.builder().url("url").title("title").price(0.0, "USD").searcher("searcher1").build()));
        when(searcher2.search(QUERY)).thenReturn(Observable.just(Offer.builder().url("url").title("title").price(0.0, "USD").searcher("searcher2").build()));

        router.handle(routingContext);

        verify(response, times(1)).end("{\"results\":["
                + "{\"url\":\"url\",\"title\":\"title\",\"searcher\":\"searcher1\",\"price\":{\"amount\":0.0,\"currency\":\"USD\"},\"requestId\":\"id\",\"type\":\"RELATED\",\"eec\":\"UNKNOWN\",\"availability\":\"NOT_AVAILABLE\"},"
                + "{\"url\":\"url\",\"title\":\"title\",\"searcher\":\"searcher2\",\"price\":{\"amount\":0.0,\"currency\":\"USD\"},\"requestId\":\"id\",\"type\":\"RELATED\",\"eec\":\"UNKNOWN\",\"availability\":\"NOT_AVAILABLE\"}"
                + "]}");
    }

    @Test
    public void sendsErrorResponseWhenSearchThrows() {
        when(searcher1.search(QUERY)).thenReturn(Observable.error(new RuntimeException("SearchError")));

        router.handle(routingContext);

        verify(routingContext, times(1)).fail(any(RuntimeException.class));
    }

}