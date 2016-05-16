package de.hanbei.rxsearch.searcher.webhose;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.ning.http.client.Request;
import de.hanbei.rxsearch.searcher.RequestBuilder;

public class WebhoseRequestBuilder implements RequestBuilder {

    private final String key;

    public WebhoseRequestBuilder(String key) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key));

        this.key = key;
    }

    @Override
    public Request createRequest(String searchInput) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchInput));
        return new com.ning.http.client.RequestBuilder("GET", true)
                .setUrl("https://webhose.io/search")
                .addQueryParam("q", searchInput)
                .addQueryParam("token", key)
                .addQueryParam("format", "json")
                .addQueryParam("size", "10")
                .build();
    }
}
