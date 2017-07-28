package rxsearch.searcher.webhose;

import rxsearch.searcher.AbstractSearcher;
import okhttp3.OkHttpClient;

public class WebhoseSearcher extends AbstractSearcher {

    public WebhoseSearcher(String name, String key, OkHttpClient asyncHttpClient) {
        super(name, new WebhoseRequestBuilder(key), new WebhoseResponseParser(name), asyncHttpClient);
    }


}
