package de.hanbei.rxsearch.server;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.SharedMetricRegistries;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ning.http.client.AsyncHttpClient;
import de.hanbei.rxsearch.config.SearcherConfiguration;
import de.hanbei.rxsearch.filter.OfferProcessor;
import de.hanbei.rxsearch.metrics.Measured;
import de.hanbei.rxsearch.searcher.Searcher;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VertxServer extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxServer.class);

    private final AsyncHttpClient asyncHttpClient;
    private final SearchRouter searchRouter;
    private final SearcherConfiguration searcherConfiguration;

    private HttpServer httpServer;

    public VertxServer() {
        asyncHttpClient = new AsyncHttpClient();
        searcherConfiguration = new SearcherConfiguration(asyncHttpClient);

        List<Searcher> searchers = searcherConfiguration.loadConfiguration("rxsearch", "testing", "de");
        List<OfferProcessor> processors = Lists.newArrayList();

        searchRouter = new SearchRouter(searchers, processors);

        ConsoleReporter reporter = ConsoleReporter.forRegistry(SharedMetricRegistries.getOrCreate(Measured.SEARCHER_METRICS))
                .shutdownExecutorOnStop(true).build();
        reporter.start(5, TimeUnit.SECONDS);
    }

    @Override
    public void start(Future<Void> fut) {
        httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route().handler(LoggerHandler.create());
        router.route().handler(ResponseTimeHandler.create());

        router.route("/search/offers").handler(TimeoutHandler.create());
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
        Integer port = 8080;

        String portAsString = System.getenv("PORT");
        if (!Strings.isNullOrEmpty(portAsString)) {
            port = Integer.parseInt(portAsString);
        } else if (config().containsKey("http.port")) {
            port = config().getInteger("http.port");
        }
        return port;
    }

    public static void main(String[] args) throws IOException {
        Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true).setJmxEnabled(true)));

        vertx.deployVerticle(VertxServer.class.getName());

        Runtime.getRuntime().addShutdownHook(new Thread(vertx::close));
    }

}
