package de.hanbei.rxsearch.searcher.duckduckgo;

import com.ning.http.client.AsyncHttpClient;
import de.hanbei.rxsearch.searcher.AbstractSearcher;

public class DuckDuckGoSearcher extends AbstractSearcher {

    public DuckDuckGoSearcher(String name, AsyncHttpClient asyncHttpClient) {
        super(name, new DuckDuckGoRequestBuilder(), new DuckDuckGoResponseParser(name), asyncHttpClient);
    }


}
