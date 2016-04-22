package de.hanbei.rxsearch.searcher.duckduckgo;

import com.ning.http.client.AsyncHttpClient;
import de.hanbei.rxsearch.searcher.AbstractSearcher;

public class DuckDuckGoSearcher extends AbstractSearcher {

    public DuckDuckGoSearcher(String name, AsyncHttpClient asyncHttpClient) {
        super(name, new DuckDuckGoResponseParser(name), asyncHttpClient);
    }

    @Override
    protected String createRequestUrl(String searchInput) {
        return "http://api.duckduckgo.com/?format=json&t=hanbeirxsearch&q=" + searchInput;
    }

}
