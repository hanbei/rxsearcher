package de.hanbei.rxsearch.server;

import com.google.common.collect.Lists;
import de.hanbei.rxsearch.events.SearchFailedEvent;
import de.hanbei.rxsearch.events.SearchFinishedEvent;
import de.hanbei.rxsearch.events.Topics;
import de.hanbei.rxsearch.model.Offer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VertxResponseHandlerTest {

    private static final String REQUEST_ID = "requestId";
    private VertxResponseHandler vertxResponseHandler;
    private RoutingContext routingContext;
    private HttpServerResponse response;
    private EventBus eventBus;

    @Before
    public void setup() {
        eventBus = mock(EventBus.class);
        response = mock(HttpServerResponse.class, RETURNS_SELF);

        routingContext = mock(RoutingContext.class, RETURNS_DEEP_STUBS);
        when(routingContext.vertx().eventBus()).thenReturn(eventBus);
        when(routingContext.response()).thenReturn(response);

        vertxResponseHandler = new VertxResponseHandler(routingContext);
    }

    @Test
    public void emptyOffersSendsNoContent() throws Exception {
        vertxResponseHandler.handleSuccess(REQUEST_ID, Lists.newArrayList());

        verify(eventBus).publish(Topics.searchFinished(), new SearchFinishedEvent(REQUEST_ID, 0));
        verify(response).putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        verify(response).setStatusCode(204);
    }

    @Test
    public void handleError() throws Exception {
        RuntimeException error = new RuntimeException("Error");
        vertxResponseHandler.handleError(REQUEST_ID, error);

        verify(eventBus).publish(Topics.searchFailed(), new SearchFailedEvent(REQUEST_ID, error));
        verify(routingContext).fail(error);
    }

    @Test
    public void someOffersSendContent() throws Exception {
        vertxResponseHandler.handleSuccess(REQUEST_ID, Lists.newArrayList(
                Offer.builder().url("").title("").price(0.0, "EUR").searcher("test").requestId(REQUEST_ID).build()
        ));

        verify(eventBus).publish(Topics.searchFinished(), new SearchFinishedEvent(REQUEST_ID, 1));
        verify(response).putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        verify(response).end("{\"results\":[{\"searcher\":\"test\",\"price\":{\"amount\":0.0,\"currency\":\"EUR\"},\"requestId\":\"requestId\",\"type\":\"RELATED\",\"eec\":\"UNKNOWN\",\"availability\":\"NOT_AVAILABLE\"}]}");
    }

}