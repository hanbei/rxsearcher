package de.hanbei.rxsearch.coordination;

import de.hanbei.rxsearch.model.Hit;

@FunctionalInterface
public interface SearcherResult {

    void searcherResult(String requestId, String searcher, Hit hit);
}
