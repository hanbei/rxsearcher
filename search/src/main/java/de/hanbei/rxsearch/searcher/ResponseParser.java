package de.hanbei.rxsearch.searcher;

import com.ning.http.client.Response;
import de.hanbei.rxsearch.model.SearchResult;
import rx.Observable;

/**
 * Parses responses from a searcher.
 */
public interface ResponseParser {

    /**
     * Returns an observable with the parsed {@link SearchResult}. If the response is not parseable an error
     * Observable that contains the exception should be returned. If the response is parseable but contains no
     * results an empty Observable should be returned
     *
     * @param response
     * @return An observable
     */
    Observable<SearchResult> toSearchResults(Response response);

}
