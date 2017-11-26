package rxsearch.searcher.webhose;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import rxsearch.model.Query;
import rxsearch.searcher.RequestBuilder;
import rxsearch.searcher.SearcherRequest;

public class WebhoseRequestBuilder implements RequestBuilder {

    private final String key;

    public WebhoseRequestBuilder(String key) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key));
        this.key = key;
    }

    @Override
    public SearcherRequest createRequest(Query query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query.getKeywords()));
        return SearcherRequest.get("https://webhose.io/search")
                .query("q", query.getKeywords())
                .query("token", key)
                .query("format", "json")
                .query("size", "10")
                .build();
    }
}
