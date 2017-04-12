package de.hanbei.rxsearch.server;

import de.hanbei.rxsearch.model.Offer;

import java.util.List;

public interface ResponseHandler {

    void handleSuccess(String requestId, List<Offer> results);

    void handleError(String requestId, Throwable t);
}
