package rxsearch.coordination;

import rxsearch.searcher.SearcherException;

@FunctionalInterface
public interface SearcherErrorHandler {

    void searcherError(String requestId, String searcher, SearcherException t);

}
