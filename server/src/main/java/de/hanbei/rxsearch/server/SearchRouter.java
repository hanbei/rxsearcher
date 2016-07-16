package de.hanbei.rxsearch.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.Searcher;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import rx.Observable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchRouter implements Handler<RoutingContext> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Searcher> searcher;

    public SearchRouter(List<Searcher> searcher) {
        this.searcher = searcher;
    }

    @Override
    public void handle(RoutingContext routingContext) {
        String keyword = routingContext.request().getParam("keyword").toLowerCase();
        String requestId = routingContext.request().getHeader("X-Request-ID");


        startSearch(routingContext, new Query(keyword, requestId));
    }

    private void startSearch(RoutingContext routingContext, Query query) {
        Observable.from(searcher)
                .flatMap(searcher -> searcher.search(query)).toList().subscribe(
                results -> {
                    try {
                        Map<String, Object> wrapper = new HashMap<>();
                        wrapper.put("results", results);
                        String s = objectMapper.writeValueAsString(wrapper);
                        HttpServerResponse response = routingContext.response();

                        if (!response.ended()) {
                            response.putHeader("Content-Type", "application/json").end(s);
                        }
                    } catch (JsonProcessingException e) {
                        routingContext.fail(e);
                    }
                },
                routingContext::fail
        );
    }

}
