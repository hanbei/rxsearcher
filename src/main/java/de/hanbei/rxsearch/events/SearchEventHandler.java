package de.hanbei.rxsearch.events;

import de.hanbei.rxsearch.coordination.SearcherCompletionHandler;
import de.hanbei.rxsearch.coordination.SearcherErrorHandler;
import de.hanbei.rxsearch.coordination.SearcherResult;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.SearcherException;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchEventHandler implements SearcherCompletionHandler, SearcherErrorHandler, SearcherResult {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchEventHandler.class);

    private final EventBus eventBus;

    public SearchEventHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void searcherCompleted(String searcher, Query query) {
        eventBus.publish("de.hanbei.searcher.finished", new SearcherFinishedEvent(searcher, query));
    }

    @Override
    public void searcherError(String searcher, SearcherException t) {
        LOGGER.warn(searcher, t);
        eventBus.publish("de.hanbei.searcher.error", new SearcherErrorEvent(searcher, t));
    }

    @Override
    public void searcherResult(String searcher, Offer offer) {
        eventBus.publish("de.hanbei.searcher.result", new SearcherResultEvent(searcher, offer));
    }
}
