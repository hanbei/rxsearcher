package de.hanbei.rxsearch.searcher;

import de.hanbei.rxsearch.model.Query;

public class SearcherException extends RuntimeException {

    private final Query query;

    public SearcherException(Query query, String message) {
        super(message);
        this.query = query;
    }

    public SearcherException(Query query, Throwable throwable) {
        super(throwable);
        this.query = query;
    }
}
