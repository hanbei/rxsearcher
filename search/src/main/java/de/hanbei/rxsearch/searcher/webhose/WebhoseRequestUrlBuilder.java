package de.hanbei.rxsearch.searcher.webhose;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import de.hanbei.rxsearch.searcher.RequestUrlBuilder;

public class WebhoseRequestUrlBuilder implements RequestUrlBuilder {

    private final String key;

    public WebhoseRequestUrlBuilder(String key) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key));

        this.key = key;
    }

    @Override
    public Request createRequestUrl(String searchInput) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchInput));
        return new RequestBuilder("GET", true)
                .setUrl("https://webhose.io/search")
                .addQueryParam("q", searchInput)
                .addQueryParam("token", key)
                .addQueryParam("format", "json")
                .addQueryParam("size", "10")
                .build();
    }
}
