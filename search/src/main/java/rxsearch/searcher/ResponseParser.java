package rxsearch.searcher;

import okhttp3.Response;
import rxsearch.model.Hit;
import io.reactivex.Observable;

/**
 * Parses responses from a searcher.
 */
public interface ResponseParser {

    /**
     * Returns an observable with the parsed {@link Hit}. If the response is not parseable an error
     * Observable that contains the exception should be returned. If the response is parseable but contains no
     * results an empty Observable should be returned
     *
     * @param response The response to parse
     * @return An observable
     */
    Observable<Hit> toSearchResults(Response response);

}
