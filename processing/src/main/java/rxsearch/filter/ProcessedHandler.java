package rxsearch.filter;

import rxsearch.model.Hit;

import java.util.List;

public interface ProcessedHandler {
    void offersFiltered(String requestId, String simpleName, boolean filter, List<Hit> remainingHits);
}
