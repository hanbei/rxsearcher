package de.hanbei.rxsearch.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.ning.http.client.AsyncHttpClient;
import de.hanbei.rxsearch.searcher.Searcher;
import de.hanbei.rxsearch.searcher.duckduckgo.DuckDuckGoSearcher;
import de.hanbei.rxsearch.searcher.github.GithubSearcher;
import de.hanbei.rxsearch.searcher.webhose.WebhoseSearcher;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import rx.Observable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hanbei on 3/25/16.
 */
public class VertxServer extends AbstractVerticle {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<Searcher> searcher;

    public VertxServer() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        searcher = Lists.newArrayList(
                new DuckDuckGoSearcher("ddgo", asyncHttpClient),
                new GithubSearcher("github", "jquery/jquery", asyncHttpClient),
                new GithubSearcher("github2", "hanbei/mock-httpserver", asyncHttpClient),
                new WebhoseSearcher("webhose", "5925ae9d-b5a2-48bf-a904-90b54604b9c2", asyncHttpClient));
    }

    @Override
    public void start(Future<Void> fut) {
        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route().handler(LoggerHandler.create());
        router.route().handler(ResponseTimeHandler.create());
//        router.route().handler(TimeoutHandler.create(6000));

        router.route("/search/:keyword").handler(routingContext -> {
            String keyword = routingContext.request().getParam("keyword").toLowerCase();
            startSearch(routingContext, keyword);
        });

        httpServer.requestHandler(router::accept).listen(8080);
    }

    private void startSearch(RoutingContext routingContext, String keyword) {
        Observable.from(searcher)
                .flatMap(searcher -> searcher.search(keyword)).toList().subscribe(
                t -> {
                    try {
                        Map<String, Object> wrapper = new HashMap<>();
                        wrapper.put("results", t);
                        String s = objectMapper.writeValueAsString(wrapper);
                        routingContext.response()
                                .putHeader("Content-Type", "application/json").end(s);
                    } catch (JsonProcessingException e) {
                        routingContext.fail(e);
                    }
                },
                routingContext::fail
        );
    }

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(VertxServer.class.getName());
    }
}
