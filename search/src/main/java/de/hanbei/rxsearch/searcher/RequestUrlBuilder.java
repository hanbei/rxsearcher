package de.hanbei.rxsearch.searcher;

import com.ning.http.client.Request;

public interface RequestUrlBuilder {

    /**
     * Takes the search parameter and builds the searcher request url. Any other parameters must be set during
     * construction.
     *
     * @param searchInput The search terms. Must not be null or empty.
     * @return The searcher url that can be requested.
     */
    Request createRequestUrl(String searchInput);
}
