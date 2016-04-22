package de.hanbei.rxsearch.searcher.github;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import de.hanbei.rxsearch.searcher.RequestUrlBuilder;

public class GithubRequestUrlBuilder implements RequestUrlBuilder {

    private final String repo;

    public GithubRequestUrlBuilder(String repo) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(repo));
        this.repo = repo;
    }

    @Override
    public String createRequestUrl(String searchInput) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(searchInput));
        return "https://api.github.com/search/code?q=" + searchInput + "+repo:" + repo;
    }
}
