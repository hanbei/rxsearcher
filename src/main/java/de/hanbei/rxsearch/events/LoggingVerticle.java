package de.hanbei.rxsearch.events;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingVerticle extends AbstractVerticle {


    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingVerticle.class);

    private MessageConsumer<SearcherCompletedEvent> finishedConsumer;
    private MessageConsumer<SearcherErrorEvent> errorConsumer;
    private MessageConsumer<SearcherResultEvent> resultConsumer;

    private MessageConsumer<SearchFinishedEvent> searchFinishedConsumer;
    private MessageConsumer<SearchFailedEvent> searchFailedConsumer;
    private MessageConsumer<SearchStartedEvent> searchStartedConsumer;

    @Override
    public void start(Future<Void> startFuture) {
        finishedConsumer = vertx.eventBus().consumer(Topics.searcherCompleted(), this::searcherCompleted);
        errorConsumer = vertx.eventBus().consumer(Topics.searcherError(), this::searcherError);
        resultConsumer = vertx.eventBus().consumer(Topics.searcherResult(), this::searcherResult);
        searchFinishedConsumer = vertx.eventBus().consumer(Topics.searchFinished(), this::searchFinished);
        searchFailedConsumer = vertx.eventBus().consumer(Topics.searchFailed(), this::searchFailed);
        searchStartedConsumer = vertx.eventBus().consumer(Topics.searchStarted(), this::searchStarted);

        LOGGER.info("Started LoggingVerticle");
        startFuture.complete();
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        finishedConsumer.unregister();
        errorConsumer.unregister();
        resultConsumer.unregister();
        searchFinishedConsumer.unregister();
        searchFailedConsumer.unregister();
        searchStartedConsumer.unregister();
        stopFuture.complete();
    }


    private void searcherCompleted(Message<SearcherCompletedEvent> message) {
        SearcherCompletedEvent event = message.body();
        LOGGER.info("{}: searcher {} completed for {}", event.getRequestId(), event.getSearcher(), event.getQuery());
    }

    private void searcherError(Message<SearcherErrorEvent> message) {
        SearcherErrorEvent event = message.body();
        //LOGGER.warn("{}: Error in searcher {}", event.getRequestId(), event.getSearcher(), event.getException());
        LOGGER.warn("{}: Error in searcher {}", event.getRequestId(), event.getSearcher(), event.getException().getMessage());
    }

    private void searcherResult(Message<SearcherResultEvent> message) {
        SearcherResultEvent event = message.body();
        LOGGER.trace("{}: searcher {} got result for {}", event.getRequestId(), event.getSearcher(), event.getOffer());
    }

    private void searchStarted(Message<SearchStartedEvent> message) {
        SearchStartedEvent event = message.body();
        LOGGER.info("{}: search started with {}", event.getRequestId(), event.getSearchConfiguraton());
    }

    private void searchFailed(Message<SearchFailedEvent> message) {
        SearchFailedEvent event = message.body();
        LOGGER.error("{}: failed search {}", event.getRequestId(), event.getError());
    }

    private void searchFinished(Message<SearchFinishedEvent> message) {
        SearchFinishedEvent event = message.body();
        LOGGER.info("{}: finished search got #{} result", event.getRequestId(), event.getNumberOfOffers());
    }

}