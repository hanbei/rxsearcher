package de.hanbei.rxsearch.events;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogSearchVerticle extends AbstractVerticle {


    private static final Logger LOGGER = LoggerFactory.getLogger(LogSearchVerticle.class);

    private MessageConsumer<SearcherCompletedEvent> finishedConsumer;
    private MessageConsumer<SearcherErrorEvent> errorConsumer;
    private MessageConsumer<SearcherResultEvent> resultConsumer;
    private MessageConsumer<SearchFinishedEvent> searchFinishedConsumer;
    private MessageConsumer<SearchFailedEvent> searchFailedConsumer;

    @Override
    public void start(Future<Void> startFuture) {
        finishedConsumer = vertx.eventBus().consumer(Topics.searcherCompleted(), this::searcherCompleted);
        errorConsumer = vertx.eventBus().consumer(Topics.searcherError(), this::searcherError);
        resultConsumer = vertx.eventBus().consumer(Topics.searcherResult(), this::searcherResult);
        searchFinishedConsumer = vertx.eventBus().consumer(Topics.searchFinished(), this::searchFinished);
        searchFailedConsumer = vertx.eventBus().consumer(Topics.searchFailed(), this::searchFailed);

        LOGGER.info("Started VertxEventVerticle");
        startFuture.complete();
    }


    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        finishedConsumer.unregister();
        errorConsumer.unregister();
        resultConsumer.unregister();
        searchFinishedConsumer.unregister();
        searchFailedConsumer.unregister();
        stopFuture.complete();
    }

    private void searcherCompleted(Message<SearcherCompletedEvent> message) {
        SearcherCompletedEvent event = message.body();
        LOGGER.info("{}: searcher {} completed for {}", event.getRequestId(), event.getSearcher(), event.getQuery());
    }

    private void searcherError(Message<SearcherErrorEvent> message) {
        SearcherErrorEvent event = message.body();
        LOGGER.warn("{}: Error in searcher {}", event.getRequestId(), event.getSearcher(), event.getException());
    }

    private void searcherResult(Message<SearcherResultEvent> message) {
        SearcherResultEvent event = message.body();
        LOGGER.info("{}: searcher {} got result for {}", event.getRequestId(), event.getSearcher(), event.getOffer());
    }

    private void searchFailed(Message<SearchFailedEvent> message) {
        SearchFailedEvent event = message.body();
        LOGGER.error("{}: failed search {}", event.getRequestId(), event.getError());
    }

    private void searchFinished(Message<SearchFinishedEvent> message) {
        SearchFinishedEvent event = message.body();
        LOGGER.info("{}: finished search {} got #{} result", event.getRequestId(), event.getNumberOfOffers());
    }

}