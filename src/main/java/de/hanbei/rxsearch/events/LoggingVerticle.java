package de.hanbei.rxsearch.events;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeoutException;

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
        finishedConsumer = vertx.eventBus().consumer(SearcherCompletedEvent.topic(), this::searcherCompleted);
        errorConsumer = vertx.eventBus().consumer(SearcherErrorEvent.topic(), this::searcherError);
        resultConsumer = vertx.eventBus().consumer(SearcherResultEvent.topic(), this::searcherResult);
        searchFinishedConsumer = vertx.eventBus().consumer(SearchFinishedEvent.topic(), this::searchFinished);
        searchFailedConsumer = vertx.eventBus().consumer(SearchFailedEvent.topic(), this::searchFailed);
        searchStartedConsumer = vertx.eventBus().consumer(SearchStartedEvent.topic(), this::searchStarted);

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
        LOGGER.trace("{}: searcher {} completed for {}", event.getRequestId(), event.getSearcher(), event.getQuery());
    }

    private void searcherError(Message<SearcherErrorEvent> message) {
        SearcherErrorEvent event = message.body();
        //LOGGER.warn("{}: Error in searcher {}", event.getRequestId(), event.getSearcher(), event.getException());

        Throwable cause = event.getException().getCause();
        if(cause instanceof TimeoutException) {
            LOGGER.warn("{}: Timeout in searcher {}: {}", event.getRequestId(), event.getSearcher(), event.getException().getMessage());
        } else {
            LOGGER.warn("{}: Error in searcher {}", event.getRequestId(), event.getSearcher(), event.getException());
        }
    }

    private void searcherResult(Message<SearcherResultEvent> message) {
        SearcherResultEvent event = message.body();
        LOGGER.trace("{}: searcher {} got result for {}", event.getRequestId(), event.getSearcher(), event.getOffer());
    }

    private void searchStarted(Message<SearchStartedEvent> message) {
        SearchStartedEvent event = message.body();
        LOGGER.trace("{}: search started with {}", event.getRequestId(), event.getSearchConfiguraton());
    }

    private void searchFailed(Message<SearchFailedEvent> message) {
        SearchFailedEvent event = message.body();
        LOGGER.warn("{}: failed search {}", event.getRequestId(), event.getError());
    }

    private void searchFinished(Message<SearchFinishedEvent> message) {
        SearchFinishedEvent event = message.body();
        LOGGER.trace("{}: finished search got #{} result", event.getRequestId(), event.getNumberOfOffers());
    }

}