package rxsearch.searcher.duckduckgo;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import rxsearch.model.Query;
import rxsearch.searcher.RequestBuilder;
import rxsearch.searcher.SearcherRequest;

import static rxsearch.searcher.Method.GET;

public class DuckDuckGoRequestBuilder implements RequestBuilder {

    @Override
    public SearcherRequest createRequest(Query query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query.getKeywords()));
        return SearcherRequest.from()
                .method(GET)
                .scheme("http")
                .host("api.duckduckgo.com")
                .query("format", "json")
                .query("t", "hanbeirxsearch")
                .query("q", query.getKeywords())
                .build();
    }

}
