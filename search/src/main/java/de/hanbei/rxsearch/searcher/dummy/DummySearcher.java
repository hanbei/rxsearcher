package de.hanbei.rxsearch.searcher.dummy;

import com.ning.http.client.AsyncHttpClient;
import de.hanbei.rxsearch.searcher.AbstractSearcher;

public class DummySearcher extends AbstractSearcher {

    public DummySearcher(String name, String serverUrl, AsyncHttpClient asyncHttpClient) {
        super(name, new DummySearcherRequestBuilder(serverUrl), new DummySearcherResponseParser(name), asyncHttpClient);
    }

}
