package de.hanbei.rxsearch.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hanbei on 3/25/16.
 */
public class VertxServer extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxServer.class);

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

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(VertxServer.class.getName());
    }

    @Override
    public void start(Future<Void> fut) {
        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route().handler(LoggerHandler.create());
        router.route().handler(ResponseTimeHandler.create());
        router.route().handler(TimeoutHandler.create(6000));

        router.route("/search/:keyword").handler(routingContext -> {
            String keyword = routingContext.request().getParam("keyword").toLowerCase();
            startSearch(routingContext, keyword);
        });

        Integer port = port();
        LOGGER.info("Starting server on "+ port);
        httpServer.requestHandler(router::accept).listen(port);
    }

    private Integer port() {
        String portAsString = System.getenv("PORT");
        Integer port = 8080;
        if (!Strings.isNullOrEmpty(portAsString)) {
            port = Integer.parseInt(portAsString);
        }
        return port;
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
}
