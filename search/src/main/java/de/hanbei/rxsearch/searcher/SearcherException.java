package de.hanbei.rxsearch.searcher;

import de.hanbei.rxsearch.model.Query;

import java.util.Objects;

public class SearcherException extends RuntimeException {

    private Query query;
    private String searcher;
    private String requestId;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SearcherException that = (SearcherException) o;
        return Objects.equals(query, that.query) &&
                Objects.equals(searcher, that.searcher) &&
                Objects.equals(requestId, that.requestId) &&
                Objects.equals(getMessage(), that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, searcher, requestId, getMessage());
    }
}
