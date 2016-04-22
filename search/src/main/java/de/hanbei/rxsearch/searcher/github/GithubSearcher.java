package de.hanbei.rxsearch.searcher.github;

import com.ning.http.client.AsyncHttpClient;
import de.hanbei.rxsearch.searcher.AbstractSearcher;

/**
 * Created by hanbei on 4/17/16.
 */
public class GithubSearcher extends AbstractSearcher {

    private final String repo;

    public GithubSearcher(String name, String repo, AsyncHttpClient asyncHttpClient) {
        super(name, new GithubResponseParser(name), asyncHttpClient);
        this.repo = repo;
    }

    @Override
    protected String createRequestUrl(String searchInput) {
        return "https://api.github.com/search/code?q=" + searchInput + "+repo:" + repo;
    }

}
