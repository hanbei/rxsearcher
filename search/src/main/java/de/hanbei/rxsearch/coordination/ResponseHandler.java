package de.hanbei.rxsearch.coordination;

import de.hanbei.rxsearch.model.Offer;
import org.slf4j.Logger;
import rx.Observable;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public interface ResponseHandler {

    static final Logger LOGGER = getLogger(ResponseHandler.class);

    void handleSuccess(List<Offer> results);

    void handleError(Throwable t);

    default Observable<Offer> searcherError(Throwable t) {
        LOGGER.warn("", t);
        return Observable.empty();
    }
}
