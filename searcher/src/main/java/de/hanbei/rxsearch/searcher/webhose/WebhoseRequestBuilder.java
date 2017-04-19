package de.hanbei.rxsearch.searcher.webhose;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.asynchttpclient.Request;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.RequestBuilder;

public class WebhoseRequestBuilder implements RequestBuilder {

    private final String key;

    public WebhoseRequestBuilder(String key) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key));

        this.key = key;
    }

    @Override
    public Request createRequest(Query query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query.getKeywords()));
        return new org.asynchttpclient.RequestBuilder("GET", true)
                .setUrl("https://webhose.io/search")
                .addQueryParam("q", query.getKeywords())
                .addQueryParam("token", key)
                .addQueryParam("format", "json")
                .addQueryParam("size", "10")
                .build();
    }
}
