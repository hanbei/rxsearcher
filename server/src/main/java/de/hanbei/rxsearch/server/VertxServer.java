package de.hanbei.rxsearch.server;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ning.http.client.AsyncHttpClient;
import de.hanbei.rxsearch.model.SearchResult;
import de.hanbei.rxsearch.searcher.duckduckgo.DuckDuckGoSearcher;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.impl.ResponseTimeHandlerImpl;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hanbei on 3/25/16.
 */
public class VertxServer extends AbstractVerticle {

    private DuckDuckGoSearcher searcher = new DuckDuckGoSearcher("ddgo", new AsyncHttpClient());
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void start(Future<Void> fut) {
        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route().handler(new ResponseTimeHandlerImpl());

        router.route("/search/:keyword").handler(routingContext -> {
            String keyword = routingContext.request().getParam("keyword").toLowerCase();
            Observable.just(searcher).flatMap(searcher -> searcher.search(keyword)).toList().subscribe(
                    t -> {
                        try {
                            Map<String, Object> wrapper = new HashMap<>();
                            wrapper.put("results", t);
                            String s = objectMapper.writeValueAsString(t);
                            routingContext.response()
                                    .putHeader("Content-Type", "application/json").end(s);
                        } catch (JsonProcessingException e) {
                            routingContext.fail(e);
                        }
                    },
                    routingContext::fail
            );

        });

        httpServer.requestHandler(router::accept).listen(8080);
    }

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(VertxServer.class.getName());
    }
}
