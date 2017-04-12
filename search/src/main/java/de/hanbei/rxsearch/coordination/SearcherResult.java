package de.hanbei.rxsearch.coordination;

import de.hanbei.rxsearch.model.Offer;

@FunctionalInterface
public interface SearcherResult {

    void searcherResult(String requestId, String searcher, Offer offer);
}
