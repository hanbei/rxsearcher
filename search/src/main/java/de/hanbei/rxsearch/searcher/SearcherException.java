package de.hanbei.rxsearch.searcher;

import de.hanbei.rxsearch.model.Query;

public class SearcherException extends RuntimeException {

    private Query query;
    private String searcher;

    public SearcherException(String message) {
        super(message);
    }

    public SearcherException(Throwable throwable) {
        super(throwable);
    }

    public static SearcherException wrap(Throwable t) {
        if (t instanceof SearcherException) {
            return (SearcherException) t;
        }
        return new SearcherException(t);
    }

    @Override
    public String getMessage() {
        return "searcher=" + searcher + ", query=" + query + ", message=" + super.getMessage();
    }

    public Query getQuery() {
        return query;
    }

    public String getSearcher() {
        return searcher;
    }

    public SearcherException query(Query query) {
        this.query = query;
        return this;
    }

    public SearcherException searcher(String searcher) {
        this.searcher = searcher;
        return this;
    }

}
