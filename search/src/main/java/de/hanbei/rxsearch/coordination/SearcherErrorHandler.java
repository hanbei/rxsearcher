package de.hanbei.rxsearch.coordination;

import de.hanbei.rxsearch.searcher.SearcherException;

@FunctionalInterface
public interface SearcherErrorHandler {

    void searcherError(String requestId, String searcher, SearcherException t);

}
