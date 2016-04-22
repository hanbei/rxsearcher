package de.hanbei.rxsearch.searcher.github;

import com.ning.http.client.AsyncHttpClient;
import de.hanbei.rxsearch.searcher.AbstractSearcher;

public class GithubSearcher extends AbstractSearcher {

    public GithubSearcher(String name, String repo, AsyncHttpClient asyncHttpClient) {
        super(name, new GithubRequestUrlBuilder(repo), new GithubResponseParser(name), asyncHttpClient);
    }


}
