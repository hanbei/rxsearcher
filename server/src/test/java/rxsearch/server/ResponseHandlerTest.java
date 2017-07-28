package rxsearch.server;

import com.google.common.collect.Lists;
import rxsearch.events.SearchFailedEvent;
import rxsearch.events.SearchFinishedEvent;
import rxsearch.model.Hit;
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

public class ResponseHandlerTest {

    private static final String REQUEST_ID = "requestId";
    private ResponseHandler responseHandler;
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

        responseHandler = new ResponseHandler();
    }

    @Test
    public void emptyOffersSendsNoContent() throws Exception {
        responseHandler.handleSuccess(routingContext, REQUEST_ID, Lists.newArrayList());

        verify(eventBus).publish(SearchFinishedEvent.topic(), new SearchFinishedEvent(REQUEST_ID, 0));
        verify(response).putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        verify(response).setStatusCode(504);
    }

    @Test
    public void handleError() throws Exception {
        RuntimeException error = new RuntimeException("Error");
        responseHandler.handleError(routingContext, REQUEST_ID, error);

        verify(eventBus).publish(SearchFailedEvent.topic(), new SearchFailedEvent(REQUEST_ID, error));
        verify(routingContext).fail(error);
    }

    @Test
    public void someOffersSendContent() throws Exception {
        responseHandler.handleSuccess(routingContext, REQUEST_ID, Lists.newArrayList(
                Hit.builder().url("").title("").searcher("test").requestId(REQUEST_ID).build()
        ));

        verify(eventBus).publish(SearchFinishedEvent.topic(), new SearchFinishedEvent(REQUEST_ID, 1));
        verify(response).putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        verify(response).end("{\"results\":[{\"searcher\":\"test\",\"requestId\":\"requestId\"}]}");
    }

}