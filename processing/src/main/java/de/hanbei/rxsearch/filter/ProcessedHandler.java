package de.hanbei.rxsearch.filter;

import de.hanbei.rxsearch.model.Hit;

import java.util.List;

public interface ProcessedHandler {
    void offersFiltered(String requestId, String simpleName, boolean filter, List<Hit> remainingHits);
}
