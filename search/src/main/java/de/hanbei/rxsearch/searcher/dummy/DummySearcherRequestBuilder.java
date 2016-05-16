package de.hanbei.rxsearch.searcher.dummy;

import com.ning.http.client.Request;
import de.hanbei.rxsearch.searcher.RequestBuilder;

public class DummySearcherRequestBuilder implements RequestBuilder {

    private String serverUrl;

    public DummySearcherRequestBuilder(String serverUrl) {
        this.serverUrl = removeSlash(serverUrl);
    }

    private String removeSlash(String serverUrl) {
        if (serverUrl.endsWith("/")) {
            return serverUrl.substring(0, serverUrl.length() - 1);
        }
        return serverUrl;
    }

    @Override
    public Request createRequest(String searchInput) {
        com.ning.http.client.RequestBuilder requestBuilder = new com.ning.http.client.RequestBuilder("GET").setUrl(serverUrl + "/search/" + searchInput);
        return requestBuilder.build();
    }
}
