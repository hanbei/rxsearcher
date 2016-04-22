package de.hanbei.rxsearch.searcher.webhose;

import com.ning.http.client.AsyncHttpClient;
import de.hanbei.rxsearch.searcher.AbstractSearcher;

public class WebhoseSearcher extends AbstractSearcher {

    private final String key;

    public WebhoseSearcher(String name, String key, AsyncHttpClient asyncHttpClient) {
        super(name, new WebhoseResponseParser(name), asyncHttpClient);
        this.key = key;
    }

    @Override
    protected String createRequestUrl(String searchInput) {
        return "https://webhose.io/search?token=" + key + "&format=json&size=10&q=" + searchInput;
    }

}
