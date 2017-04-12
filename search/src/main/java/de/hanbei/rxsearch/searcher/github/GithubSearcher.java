package de.hanbei.rxsearch.searcher.github;

import org.asynchttpclient.AsyncHttpClient;
import de.hanbei.rxsearch.searcher.AbstractSearcher;

public class GithubSearcher extends AbstractSearcher {

    public GithubSearcher(String name, String repo, AsyncHttpClient asyncHttpClient) {
        super(name, new GithubRequestBuilder(repo), new GithubResponseParser(name), asyncHttpClient);
    }


}
