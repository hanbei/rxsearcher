package de.hanbei.rxsearch.searcher.duckduckgo;

import de.hanbei.rxsearch.searcher.AbstractSearcher;
import okhttp3.OkHttpClient;

public class DuckDuckGoSearcher extends AbstractSearcher {

    public DuckDuckGoSearcher(String name, OkHttpClient asyncHttpClient) {
        super(name, new DuckDuckGoRequestBuilder(), new DuckDuckGoResponseParser(name), asyncHttpClient);
    }


}
