package rxsearch.server;

import rxsearch.events.SearchFailedEvent;
import rxsearch.events.SearchFinishedEvent;
import rxsearch.model.Hit;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ResponseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHandler.class);
    private static final String MEDIATYPE_JSON = "application/json";

    public void handleSuccess(RoutingContext routingContext, String requestId, List<Hit> results) {
        routingContext.vertx().eventBus().publish(SearchFinishedEvent.topic(), new SearchFinishedEvent(requestId, results.size()));

        if (results.isEmpty()) {
            sendNoContent(routingContext);
            return;
        }

        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("results", results);
        String s = Json.encode(wrapper);
        sendResponse(routingContext, s);
    }

    private void sendNoContent(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();

        if (!response.ended()) {
            response.putHeader(HttpHeaders.CONTENT_TYPE, MEDIATYPE_JSON).setStatusCode(504).end();
        } else {
            logResponseAlreadyEnded();
        }
    }

    public void handleError(RoutingContext routingContext, String requestId, Throwable t) {
        LOGGER.warn(t.getMessage());
        routingContext.vertx().eventBus().publish(SearchFailedEvent.topic(), new SearchFailedEvent(requestId, t));
        routingContext.fail(t);
    }

    private void sendResponse(RoutingContext routingContext, String s) {
        HttpServerResponse response = routingContext.response();

        if (!response.ended()) {
            response.putHeader(HttpHeaders.CONTENT_TYPE, MEDIATYPE_JSON).end(s);
        } else {
            logResponseAlreadyEnded();
        }
    }

    private void logResponseAlreadyEnded() {
        LOGGER.warn("Reponse already ended");
    }

}
