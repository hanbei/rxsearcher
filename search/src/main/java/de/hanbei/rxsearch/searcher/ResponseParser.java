package de.hanbei.rxsearch.searcher;

import de.hanbei.rxsearch.model.SearchResult;
import rx.Observable;

/**
 * Parses responses from a searcher.
 */
public interface ResponseParser {

    /**
     * Returns an observable with the parsed {@link SearchResult}responseAsString. If the response is not parseable an error
     * Observable that contains the exception should be returned. If the response is parseable but contains no
     * results an empty Observable should be returned
     *
     * @param responseAsString
     * @return An observable
     */
    Observable<SearchResult> toSearchResults(String responseAsString);

}
