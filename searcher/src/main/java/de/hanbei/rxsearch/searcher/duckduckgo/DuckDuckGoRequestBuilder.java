package de.hanbei.rxsearch.searcher.duckduckgo;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.RequestBuilder;
import okhttp3.HttpUrl;
import okhttp3.Request;

public class DuckDuckGoRequestBuilder implements RequestBuilder {

    @Override
    public Request createRequest(Query query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query.getKeywords()));
        return new Request.Builder().get()
                .url(new HttpUrl.Builder().scheme("http").host("api.duckduckgo.com")
                        .addQueryParameter("format", "json")
                        .addQueryParameter("t", "hanbeirxsearch")
                        .addQueryParameter("q", query.getKeywords())
                        .build())
                .build();
    }

}
