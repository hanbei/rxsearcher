package de.hanbei.rxsearch.server;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ning.http.client.AsyncHttpClient;
import de.hanbei.rxsearch.searcher.duckduckgo.DuckDuckGoSearcher;
import de.hanbei.rxsearch.searcher.github.GithubSearcher;
import de.hanbei.rxsearch.searcher.webhose.WebhoseSearcher;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxServer extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxServer.class);

    private final AsyncHttpClient asyncHttpClient;
    private final SearchRouter searchRouter;


    public VertxServer() {
        asyncHttpClient = new AsyncHttpClient();
        searchRouter = new SearchRouter(Lists.newArrayList(
                new DuckDuckGoSearcher("ddgo", asyncHttpClient),
                new GithubSearcher("github", "jquery/jquery", asyncHttpClient),
                new GithubSearcher("github2", "hanbei/mock-httpserver", asyncHttpClient),
                new WebhoseSearcher("webhose", "5925ae9d-b5a2-48bf-a904-90b54604b9c2", asyncHttpClient)));

    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
                new DropwizardMetricsOptions().setEnabled(true).setJmxEnabled(true)));
        vertx.deployVerticle(VertxServer.class.getName());
    }

    @Override
    public void start(Future<Void> fut) {
        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route().handler(LoggerHandler.create());
        router.route().handler(ResponseTimeHandler.create());
        router.route().handler(TimeoutHandler.create(6000));

        router.route("/search/:keyword").handler(searchRouter);

        Integer port = port();
        LOGGER.info("Starting server on " + port);
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

}
