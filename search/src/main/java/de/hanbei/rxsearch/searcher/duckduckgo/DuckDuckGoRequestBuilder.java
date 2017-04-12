package de.hanbei.rxsearch.searcher.duckduckgo;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.asynchttpclient.Request;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.RequestBuilder;

public class DuckDuckGoRequestBuilder implements RequestBuilder {

    @Override
    public Request createRequest(Query query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query.getKeywords()));
        return new org.asynchttpclient.RequestBuilder("GET")
                .setUrl("http://api.duckduckgo.com")
                .addQueryParam("format", "json")
                .addQueryParam("t", "hanbeirxsearch")
                .addQueryParam("q", query.getKeywords())
                .build();
    }

}
