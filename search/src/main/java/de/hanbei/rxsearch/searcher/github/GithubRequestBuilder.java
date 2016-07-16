package de.hanbei.rxsearch.searcher.github;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.ning.http.client.Request;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.RequestBuilder;

public class GithubRequestBuilder implements RequestBuilder {

    private final String repo;

    public GithubRequestBuilder(String repo) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(repo));
        this.repo = repo;
    }

    @Override
    public Request createRequest(Query query) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(query.keywords()));
        return new com.ning.http.client.RequestBuilder("GET", true)
                .setUrl("https://api.github.com/search/code")
                .addQueryParam("q", query.keywords() + "+repo:" + repo)
                .build();
    }
}
