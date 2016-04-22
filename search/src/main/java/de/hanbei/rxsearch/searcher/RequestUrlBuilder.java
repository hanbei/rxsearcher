package de.hanbei.rxsearch.searcher;

public interface RequestUrlBuilder {

    /**
     * Takes the search parameter and builds the searcher request url. Any other parameters must be set during
     * construction.
     *
     * @param searchInput The search terms. Must not be null or empty.
     * @return The searcher url that can be requested.
     */
    String createRequestUrl(String searchInput);
}
