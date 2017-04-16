package de.hanbei.rxsearch.server;

import de.hanbei.rxsearch.events.SearchFailedEvent;
import de.hanbei.rxsearch.events.SearchFinishedEvent;
import de.hanbei.rxsearch.model.Offer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class VertxResponseHandler implements ResponseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxResponseHandler.class);
    private static final String MEDIATYPE_JSON = "application/json";

    private final RoutingContext routingContext;
    private final EventBus eventBus;

    VertxResponseHandler(RoutingContext routingContext) {
        this.routingContext = routingContext;
        this.eventBus = routingContext.vertx().eventBus();
    }

    @Override
    public void handleSuccess(String requestId, List<Offer> results) {
        eventBus.publish(SearchFinishedEvent.topic(), new SearchFinishedEvent(requestId, results.size()));

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
            response.putHeader(HttpHeaders.CONTENT_TYPE, MEDIATYPE_JSON).setStatusCode(204).end();
        } else {
            logResponseAlreadyEnded();
        }
    }

    @Override
    public void handleError(String requestId, Throwable t) {
        LOGGER.warn(t.getMessage());
        eventBus.publish(SearchFailedEvent.topic(), new SearchFailedEvent(requestId, t));
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
