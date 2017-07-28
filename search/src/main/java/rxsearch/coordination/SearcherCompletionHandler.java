package rxsearch.coordination;

import rxsearch.model.Query;

@FunctionalInterface
public interface SearcherCompletionHandler {

    void searcherCompleted(String requestId, String searcher, Query query);

}
