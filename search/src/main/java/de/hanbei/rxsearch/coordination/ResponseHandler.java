package de.hanbei.rxsearch.coordination;

import de.hanbei.rxsearch.model.Offer;

import java.util.List;

public interface ResponseHandler {
    void handleSuccess(List<Offer> results);

    void handleError(Throwable t);
}
