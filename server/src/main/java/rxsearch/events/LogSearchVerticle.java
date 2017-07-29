package rxsearch.events;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LogSearchVerticle extends AbstractVerticle {


    private static final Logger LOGGER = LoggerFactory.getLogger(LogSearchVerticle.class);

    private MessageConsumer<SearcherErrorEvent> errorConsumer;
    private MessageConsumer<SearcherResultEvent> resultConsumer;
    private MessageConsumer<SearchFinishedEvent> searchFinishedConsumer;
    private MessageConsumer<SearchFailedEvent> searchFailedConsumer;
    private MessageConsumer<SearchStartedEvent> searchStartedConsumer;
    private MessageConsumer<OfferProcessedEvent> processedConsumer;

    private final Map<String, LoggedSearchContainer> loggedSearchesContainer;

    public LogSearchVerticle() {
        this.loggedSearchesContainer = new HashMap<>();
    }

    @Override
    public void start(Future<Void> startFuture) {
        LOGGER.info("Started LogSearchVerticle");

        errorConsumer = vertx.eventBus().consumer(SearcherErrorEvent.topic(), this::searcherGotError);
        resultConsumer = vertx.eventBus().consumer(SearcherResultEvent.topic(), this::searcherGotResult);

        searchFinishedConsumer = vertx.eventBus().consumer(SearchFinishedEvent.topic(), this::searchFinished);
        searchFailedConsumer = vertx.eventBus().consumer(SearchFailedEvent.topic(), this::searchFailed);
        searchStartedConsumer = vertx.eventBus().consumer(SearchStartedEvent.topic(), this::searchStarted);

        processedConsumer = vertx.eventBus().consumer(OfferProcessedEvent.topic(), this::offerProcessed);

        startFuture.complete();
    }

    private void offerProcessed(Message<OfferProcessedEvent> message) {
        OfferProcessedEvent event = message.body();
        Optional<LoggedSearchContainer> loggedSearchContainer = getLoggedSearchContainer(event.getRequestId());
        loggedSearchContainer.ifPresent(loggedSearchContainer1 -> loggedSearchContainer1.addProcessor(event.getProcessor(), event.isFilter(), event.getHits()));
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        errorConsumer.unregister();
        resultConsumer.unregister();
        searchFinishedConsumer.unregister();
        searchFailedConsumer.unregister();
        searchStartedConsumer.unregister();
        processedConsumer.unregister();
        stopFuture.complete();
    }

    private void searchStarted(Message<SearchStartedEvent> message) {
        SearchStartedEvent event = message.body();
        if (event.getSearchConfiguraton().getLogSearch()) {
            LoggedSearchContainer loggedSearch = loggedSearchesContainer.getOrDefault(event.getRequestId(),
                    new LoggedSearchContainer(event.getRequestId(), event.getSearchConfiguraton()));
            loggedSearchesContainer.put(event.getRequestId(), loggedSearch);
        }
    }

    private void searchFailed(Message<SearchFailedEvent> message) {
        SearchFailedEvent event = message.body();
        Optional<LoggedSearchContainer> loggedSearchContainer = getLoggedSearchContainer(event.getRequestId());
        loggedSearchContainer.map(lsc -> {
            lsc.failed(event.getError());
            return lsc;
        }).ifPresent(lsc -> LOGGER.info("{}", Json.encodePrettily(lsc)));
    }

    private void searchFinished(Message<SearchFinishedEvent> message) {
        SearchFinishedEvent event = message.body();
        getLoggedSearchContainer(event.getRequestId()).ifPresent(lsc -> {
            lsc.success(event.getNumberOfOffers());
            LOGGER.info("{}", Json.encode(lsc));
            loggedSearchesContainer.remove(event.getRequestId());
        });
    }

    private void searcherGotResult(Message<SearcherResultEvent> message) {
        SearcherResultEvent event = message.body();
        Optional<LoggedSearchContainer> loggedSearchContainer = getLoggedSearchContainer(event.getRequestId());
        loggedSearchContainer.ifPresent(lsc -> {
            lsc.addOffer(event.getSearcher(), event.getHit());
            loggedSearchesContainer.put(event.getRequestId(), lsc);
        });
    }

    private Optional<LoggedSearchContainer> getLoggedSearchContainer(String requestId) {
        LoggedSearchContainer loggedSearchContainer = loggedSearchesContainer.get(requestId);
        if (!loggedSearchContainer.getSearchConfiguraton().getLogSearch()) {
            return Optional.empty();
        }
        return Optional.ofNullable(loggedSearchContainer);
    }

    private void searcherGotError(Message<SearcherErrorEvent> message) {
        // log error
    }

}