package de.hanbei.rxsearch.coordination;

import de.hanbei.rxsearch.model.Offer;

@FunctionalInterface
public interface SearcherResult {

    void searcherResult(String searcher, Offer offer);
}
