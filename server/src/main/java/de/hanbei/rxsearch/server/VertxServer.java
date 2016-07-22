package de.hanbei.rxsearch.server;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ning.http.client.AsyncHttpClient;
import de.hanbei.rxsearch.searcher.fred.FredSearcher;
import de.hanbei.rxsearch.searcher.zoom.ZoomSearcher;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.TimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class VertxServer extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxServer.class);

    private final AsyncHttpClient asyncHttpClient;
    private final SearchRouter searchRouter;
    private HttpServer httpServer;

    public VertxServer() {
        asyncHttpClient = new AsyncHttpClient();
        searchRouter = new SearchRouter(Lists.newArrayList(
                new FredSearcher("dummy1", "http://dummysearcher1.herokuapp.com", asyncHttpClient),
                new FredSearcher("dummy2", "http://dummysearcher2.herokuapp.com", asyncHttpClient),
                new ZoomSearcher("zoom", "http://dummysearcher3.herokuapp.com/search/zoom", asyncHttpClient)
        ));

    }

    public static void main(String[] args) throws IOException {
        Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true).setJmxEnabled(true)));

        vertx.deployVerticle(VertxServer.class.getName());

        Runtime.getRuntime().addShutdownHook(new Thread(vertx::close));
    }

    @Override
    public void start(Future<Void> fut) {
        httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route().handler(LoggerHandler.create());
        router.route().handler(ResponseTimeHandler.create());
        router.route().handler(TimeoutHandler.create(6000));

        router.route("/search/offers").handler(searchRouter);

        router.route().handler(StaticHandler.create()
                .setWebRoot("apidocs")
                .setFilesReadOnly(false)
                .setCachingEnabled(false));

        Integer port = port();
        httpServer.requestHandler(router::accept).listen(port, result -> {
            if (result.succeeded()) {
                fut.complete();
                LOGGER.info("Started server on {}", port);
            } else {
                LOGGER.info("Failed starting server: {}", result.cause());
                fut.fail(result.cause());
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        LOGGER.info("Stopping server");
        asyncHttpClient.close();
        httpServer.close();
        super.stop(stopFuture);
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
