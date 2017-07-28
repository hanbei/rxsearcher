package rxsearch.searcher.duckduckgo;

import rxsearch.searcher.AbstractSearcher;
import okhttp3.OkHttpClient;

public class DuckDuckGoSearcher extends AbstractSearcher {

    public DuckDuckGoSearcher(String name, OkHttpClient asyncHttpClient) {
        super(name, new DuckDuckGoRequestBuilder(), new DuckDuckGoResponseParser(name), asyncHttpClient);
    }


}
