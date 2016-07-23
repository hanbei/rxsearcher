package de.hanbei.rxsearch.coordination;

import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.searcher.SearcherException;
import org.slf4j.Logger;
import rx.Observable;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public interface ResponseHandler {

    Logger LOGGER = getLogger(ResponseHandler.class);

    void handleSuccess(List<Offer> results);

    void handleError(Throwable t);

    default Observable<Offer> searcherError(SearcherException t) {
        LOGGER.warn("message: {}, query: {}", t.getMessage(), t.getQuery());
        return Observable.empty();
    }
}
