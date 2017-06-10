package de.hanbei.rxsearch.server;

import de.hanbei.rxsearch.events.SearchFailedEvent;
import de.hanbei.rxsearch.events.SearchFinishedEvent;
import de.hanbei.rxsearch.events.SearchStartedEvent;
import de.hanbei.rxsearch.events.SearcherCompletedEvent;
import de.hanbei.rxsearch.events.SearcherResultEvent;
import de.hanbei.rxsearch.model.Hit;
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
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class SearchRouterTest {

    private static final String SEARCH_TERM = "search_term";
    private static final String ID = "id";
    private static final String DE = "de";
    private static final Query QUERY = Query.builder().keywords(SEARCH_TERM).requestId(ID).country(DE).user(new User("user_id", "partner_id", "partner_sub_id")).build();
    private static final Hit hit1 = Hit.builder().url("url").title("title").searcher("searcher1").build();
    private static final Hit hit2 = Hit.builder().url("url").title("title").searcher("searcher2").build();

    private SearchRouter router;
    private Searcher searcher1;
    private Searcher searcher2;

    private HttpServerResponse response;
    private EventBus eventBus;
    private RoutingContext routingContext;


    @Before
    public void setUp() {
        eventBus = mock(EventBus.class);

        searcher1 = mock(Searcher.class);
        when(searcher1.getName()).thenReturn("searcher1");
        searcher2 = mock(Searcher.class);
        when(searcher2.getName()).thenReturn("searcher2");

        HttpServerRequest request = mock(HttpServerRequest.class);
        when(request.getHeader("X-Request-ID")).thenReturn(ID);

        response = mock(HttpServerResponse.class, RETURNS_SELF);
        when(response.putHeader(eq(HttpHeaders.CONTENT_TYPE), anyString())).thenReturn(response);

        routingContext = mock(RoutingContext.class, RETURNS_DEEP_STUBS);
        when(routingContext.request()).thenReturn(request);
        when(routingContext.response()).thenReturn(response);
        when(routingContext.getBodyAsJson()).thenReturn(new JsonObject(
                "{\"query\":{\n" +
                        "  \"keywords\":\"" + SEARCH_TERM + "\",\n" +
                        "  \"country\":\"" + DE + "\"," +
                        "  \"user\":{" +
                        "     \"id\":\"user_id\"," +
                        "     \"partnerId\":\"partner_id\"," +
                        "     \"partnerSubId\":\"partner_sub_id\"" +
                        "  }" +
                        "}}"));
        when(routingContext.vertx().eventBus()).thenReturn(eventBus);

        router = new SearchRouter(newArrayList(searcher1, searcher2), newArrayList(), eventBus);
    }

    @Test
    public void whenSearcherRespondRendersCorrectJson() {
        when(searcher1.search(QUERY)).thenReturn(Observable.just(hit1));
        when(searcher2.search(QUERY)).thenReturn(Observable.just(hit2));

        router.handle(routingContext);

        verify(response, times(1)).end("{\"results\":["
                + "{\"url\":\"url\",\"title\":\"title\",\"searcher\":\"searcher1\",\"price\":{\"amount\":0.0,\"currency\":\"USD\"},\"requestId\":\"id\",\"type\":\"RELATED\",\"eec\":\"UNKNOWN\",\"availability\":\"NOT_AVAILABLE\"},"
                + "{\"url\":\"url\",\"title\":\"title\",\"searcher\":\"searcher2\",\"price\":{\"amount\":0.0,\"currency\":\"USD\"},\"requestId\":\"id\",\"type\":\"RELATED\",\"eec\":\"UNKNOWN\",\"availability\":\"NOT_AVAILABLE\"}"
                + "]}");
    }

    @Test
    public void testEventsArePublishedIfSearchReturnsCorrectly() {
        when(searcher1.search(QUERY)).thenReturn(Observable.just(hit1));
        when(searcher2.search(QUERY)).thenReturn(Observable.just(hit2));

        router.handle(routingContext);

        verify(eventBus).publish(SearchStartedEvent.topic(), new SearchStartedEvent(ID, new SearchRequestConfiguration(ID, false)));
        verify(eventBus).publish(SearcherCompletedEvent.topic(), new SearcherCompletedEvent(ID, "searcher1", QUERY));
        verify(eventBus).publish(SearcherCompletedEvent.topic(), new SearcherCompletedEvent(ID, "searcher2", QUERY));
        verify(eventBus).publish(SearcherResultEvent.topic(), new SearcherResultEvent(ID, "searcher1", hit1));
        verify(eventBus).publish(SearcherResultEvent.topic(), new SearcherResultEvent(ID, "searcher2", hit2));
        verify(eventBus).publish(SearchFinishedEvent.topic(), new SearchFinishedEvent(ID, 2));
    }

    @Test
    public void testEventsArePublishedIfSearchFails() {
        RuntimeException searchError = new RuntimeException("SearchError");
        when(searcher1.search(QUERY)).thenThrow(searchError);
        when(searcher2.search(QUERY)).thenReturn(Observable.empty());

        router.handle(routingContext);

        verify(eventBus).publish(SearchStartedEvent.topic(), new SearchStartedEvent(ID, new SearchRequestConfiguration(ID, false)));
        verify(eventBus).publish(SearchFailedEvent.topic(), new SearchFailedEvent(ID, searchError));
        verifyNoMoreInteractions(eventBus);
    }

    @Test
    public void noResultsSend204() {
        when(searcher1.search(QUERY)).thenReturn(Observable.empty());
        when(searcher2.search(QUERY)).thenReturn(Observable.empty());

        router.handle(routingContext);

        verify(response, times(1)).setStatusCode(504);

        verify(eventBus).publish(SearchStartedEvent.topic(), new SearchStartedEvent(ID, new SearchRequestConfiguration(ID, false)));
        verify(eventBus).publish(SearchFinishedEvent.topic(), new SearchFinishedEvent(ID, 0));

    }

    @Test
    public void sendsErrorResponseWhenSearchThrows() {
        when(searcher1.search(QUERY)).thenReturn(Observable.error(new RuntimeException("SearchError")));
        // searcher2 throws a NPE here thats why it fails.

        router.handle(routingContext);

        verify(routingContext, times(1)).fail(any(RuntimeException.class));
    }

}