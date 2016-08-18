package de.hanbei.rxsearch.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hanbei.rxsearch.coordination.SearcherErrorHandler;
import de.hanbei.rxsearch.model.Offer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
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
    private final ObjectMapper objectMapper;


    VertxResponseHandler(RoutingContext routingContext) {
        this.routingContext = routingContext;

        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handleSuccess(List<Offer> results) {
        if (results.isEmpty()) {
            sendNoContent(routingContext);
            return;
        }

        try {
            Map<String, Object> wrapper = new HashMap<>();
            wrapper.put("results", results);
            String s = objectMapper.writeValueAsString(wrapper);
            sendResponse(routingContext, s);
        } catch (JsonProcessingException e) {
            handleError(e);
        }

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
    public void handleError(Throwable t) {
        LOGGER.warn(t.getMessage());
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
