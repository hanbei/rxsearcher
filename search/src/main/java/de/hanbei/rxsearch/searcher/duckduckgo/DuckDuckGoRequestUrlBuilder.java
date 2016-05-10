package de.hanbei.rxsearch.searcher.duckduckgo;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import de.hanbei.rxsearch.searcher.RequestUrlBuilder;

public class DuckDuckGoRequestUrlBuilder implements RequestUrlBuilder {

    @Override
    public Request createRequestUrl(String searchInput) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchInput));
        return new RequestBuilder("GET")
                .setUrl("http://api.duckduckgo.com")
                .addQueryParam("format", "json")
                .addQueryParam("t", "hanbeirxsearch")
                .addQueryParam("q", searchInput)
                .build();
    }

}
