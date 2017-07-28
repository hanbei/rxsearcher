package rxsearch.coordination;

import rxsearch.model.Hit;

@FunctionalInterface
public interface SearcherResult {

    void searcherResult(String requestId, String searcher, Hit hit);
}
