package de.hanbei.rxsearch.searcher.github;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.ning.http.client.Request;
import com.ning.http.client.RequestBuilder;
import de.hanbei.rxsearch.searcher.RequestUrlBuilder;

public class GithubRequestUrlBuilder implements RequestUrlBuilder {

    private final String repo;

    public GithubRequestUrlBuilder(String repo) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(repo));
        this.repo = repo;
    }

    @Override
    public Request createRequestUrl(String searchInput) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchInput));
        return new RequestBuilder("GET", true)
                .setUrl("https://api.github.com/search/code")
                .addQueryParam("q", searchInput + "+repo:" + repo)
                .build();
    }
}
