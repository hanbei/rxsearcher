package de.hanbei.rxsearch.coordination;

import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.searcher.SearcherException;
import org.slf4j.Logger;
import rx.Observable;

import static org.slf4j.LoggerFactory.getLogger;

public interface SearcherErrorHandler {

    Logger LOGGER = getLogger(SearcherErrorHandler.class);

    default Observable<Offer> searcherError(SearcherException t) {
        LOGGER.warn("Error in searcher", t);
        return Observable.empty();
    }
}
