package de.hanbei.rxsearch.searcher.webhose;

import org.asynchttpclient.AsyncHttpClient;
import de.hanbei.rxsearch.searcher.AbstractSearcher;

public class WebhoseSearcher extends AbstractSearcher {

    public WebhoseSearcher(String name, String key, AsyncHttpClient asyncHttpClient) {
        super(name, new WebhoseRequestBuilder(key), new WebhoseResponseParser(name), asyncHttpClient);
    }


}
