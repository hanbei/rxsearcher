package rxsearch.searcher.github;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import rxsearch.model.Query;
import rxsearch.searcher.RequestBuilder;
import rxsearch.searcher.SearcherRequest;

public class GithubRequestBuilder implements RequestBuilder {

    private final String repo;

    public GithubRequestBuilder(String repo) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(repo));
        this.repo = repo;
    }

    @Override
    public SearcherRequest createRequest(Query query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query.getKeywords()));
        return SearcherRequest.get("https://api.github.com/search/code")
                .query("q", query.getKeywords() + "+repo:" + repo)
                .build();
    }
}
