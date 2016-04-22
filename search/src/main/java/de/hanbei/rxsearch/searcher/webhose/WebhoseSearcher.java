package de.hanbei.rxsearch.searcher.webhose;

import com.ning.http.client.AsyncHttpClient;
import de.hanbei.rxsearch.searcher.AbstractSearcher;

public class WebhoseSearcher extends AbstractSearcher {

    public WebhoseSearcher(String name, String key, AsyncHttpClient asyncHttpClient) {
        super(name, new WebhoseRequestUrlBuilder(key), new WebhoseResponseParser(name), asyncHttpClient);
    }


}
