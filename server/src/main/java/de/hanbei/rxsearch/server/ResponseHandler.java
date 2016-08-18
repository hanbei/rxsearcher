package de.hanbei.rxsearch.server;

import de.hanbei.rxsearch.model.Offer;

import java.util.List;

public interface ResponseHandler {

    public void handleSuccess(List<Offer> results);

    public void handleError(Throwable t);
}
