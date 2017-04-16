package de.hanbei.rxsearch.events;

import de.hanbei.rxsearch.coordination.SearcherCompletionHandler;
import de.hanbei.rxsearch.coordination.SearcherErrorHandler;
import de.hanbei.rxsearch.coordination.SearcherResult;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.SearcherException;
import io.vertx.core.eventbus.EventBus;

public class SearchEventHandler implements SearcherCompletionHandler, SearcherErrorHandler, SearcherResult {

    private final EventBus eventBus;

    public SearchEventHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void searcherCompleted(String requestId, String searcher, Query query) {
        eventBus.publish(SearcherCompletedEvent.topic(), new SearcherCompletedEvent(requestId, searcher, query));
    }

    @Override
    public void searcherError(String requestId, String searcher, SearcherException t) {
        eventBus.publish(SearcherErrorEvent.topic(), new SearcherErrorEvent(requestId, searcher, t));
    }

    @Override
    public void searcherResult(String requestId, String searcher, Offer offer) {
        eventBus.publish(SearcherResultEvent.topic(), new SearcherResultEvent(requestId, searcher, offer));
    }
}
