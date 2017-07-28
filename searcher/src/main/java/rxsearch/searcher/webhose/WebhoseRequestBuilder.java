package rxsearch.searcher.webhose;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import rxsearch.model.Query;
import rxsearch.searcher.RequestBuilder;
import okhttp3.HttpUrl;
import okhttp3.Request;

public class WebhoseRequestBuilder implements RequestBuilder {

    private final String key;

    public WebhoseRequestBuilder(String key) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key));
        this.key = key;
    }

    @Override
    public Request createRequest(Query query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query.getKeywords()));
        return new Request.Builder().get()
                .url(HttpUrl.parse("https://webhose.io/search").newBuilder()
                        .addQueryParameter("q", query.getKeywords())
                        .addQueryParameter("token", key)
                        .addQueryParameter("format", "json")
                        .addQueryParameter("size", "10")
                        .build()).build();
    }
}
