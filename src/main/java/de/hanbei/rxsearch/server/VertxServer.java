package de.hanbei.rxsearch.server;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.SharedMetricRegistries;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ning.http.client.AsyncHttpClient;
import de.hanbei.rxsearch.config.SearcherConfiguration;
import de.hanbei.rxsearch.events.LogSearchVerticle;
import de.hanbei.rxsearch.events.LoggingVerticle;
import de.hanbei.rxsearch.events.OfferProcessedEvent;
import de.hanbei.rxsearch.events.SearchFailedEvent;
import de.hanbei.rxsearch.events.SearchFinishedEvent;
import de.hanbei.rxsearch.events.SearchStartedEvent;
import de.hanbei.rxsearch.events.SearcherCompletedEvent;
import de.hanbei.rxsearch.events.SearcherErrorEvent;
import de.hanbei.rxsearch.events.SearcherResultEvent;
import de.hanbei.rxsearch.filter.OfferFilter;
import de.hanbei.rxsearch.filter.OfferProcessor;
import de.hanbei.rxsearch.metrics.Measured;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.Searcher;
import io.reactivex.Observable;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
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
    private final List<Searcher> searchers;
    private final List<OfferProcessor> processors;

    private SearchRouter searchRouter;
    private HttpServer httpServer;


    public VertxServer() {
        asyncHttpClient = new AsyncHttpClient();
        SearcherConfiguration searcherConfiguration = new SearcherConfiguration(asyncHttpClient);

        searchers = searcherConfiguration.loadConfiguration("rxsearch", "testing", "de");
        processors = Lists.newArrayList((OfferFilter) (Query q, Observable<Offer> o) -> o.filter(offer -> offer.getPrice().getAmount() > 2500));


        ConsoleReporter reporter = ConsoleReporter.forRegistry(SharedMetricRegistries.getOrCreate(Measured.SEARCHER_METRICS))
                .shutdownExecutorOnStop(true).build();
        //reporter.start(5, TimeUnit.SECONDS);
    }

    @Override
    public void start(Future<Void> fut) {
        searchRouter = new SearchRouter(searchers, processors, vertx.eventBus());

        httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route().handler(LoggerHandler.create());
        router.route().handler(ResponseTimeHandler.create());


        router.route("/search/offers").handler(BodyHandler.create());
        router.route("/search/offers").handler(TimeoutHandler.create());
        router.post("/search/offers").handler(searchRouter);

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
        Stopwatch stopwatch = Stopwatch.createStarted();

        Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(new DropwizardMetricsOptions().setEnabled(true).setJmxEnabled(true)));

        Json.mapper.registerModule(new GuavaModule());
        Json.mapper.registerModule(new KotlinModule());
        Json.prettyMapper.registerModule(new GuavaModule());
        Json.prettyMapper.registerModule(new KotlinModule());

        vertx.eventBus().registerDefaultCodec(SearcherCompletedEvent.class, SearcherCompletedEvent.Codec());
        vertx.eventBus().registerDefaultCodec(SearcherErrorEvent.class, SearcherErrorEvent.Codec());
        vertx.eventBus().registerDefaultCodec(SearcherResultEvent.class, SearcherResultEvent.Codec());
        vertx.eventBus().registerDefaultCodec(SearchFinishedEvent.class, SearchFinishedEvent.Codec());
        vertx.eventBus().registerDefaultCodec(SearchFailedEvent.class, SearchFailedEvent.Codec());
        vertx.eventBus().registerDefaultCodec(SearchStartedEvent.class, SearchStartedEvent.Codec());
        vertx.eventBus().registerDefaultCodec(OfferProcessedEvent.class, OfferProcessedEvent.Codec());

        vertx.deployVerticle(LogSearchVerticle.class.getName());
        vertx.deployVerticle(LoggingVerticle.class.getName());
        vertx.deployVerticle(VertxServer.class.getName());

        stopwatch.stop();
        LOGGER.info("Startup in {}", stopwatch.elapsed(TimeUnit.MILLISECONDS));

        Runtime.getRuntime().addShutdownHook(new Thread(vertx::close));
    }

}
