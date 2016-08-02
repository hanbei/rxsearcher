package de.hanbei.rxsearch.config

import com.ning.http.client.AsyncHttpClient
import de.hanbei.rxsearch.searcher.duckduckgo.DuckDuckGoSearcher
import de.hanbei.rxsearch.searcher.fred.FredSearcher
import de.hanbei.rxsearch.searcher.github.GithubSearcher
import de.hanbei.rxsearch.searcher.webhose.WebhoseSearcher
import de.hanbei.rxsearch.searcher.zoom.ZoomSearcher

class SearchConfigBuilder extends BuilderSupport {

    public static final String NAME = "name"
    private AsyncHttpClient asyncHttpClient

    SearchConfigBuilder(AsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient
    }

    @Override
    protected void setParent(Object parent, Object child) {
        if (parent instanceof Searchers) {
            ((Searchers) parent).searchers.add(child)
        }
    }

    @Override
    protected Object createNode(Object name) {
        return createNode(name, null, null)
    }

    @Override
    protected Object createNode(Object name, Object value) {
        return createNode(name, null, value)
    }

    @Override
    protected Object createNode(Object name, Map attributes) {
        return createNode(name, attributes, null)
    }

    @Override
    protected Object createNode(Object name, Map attributes, Object value) {
        switch (name) {
            case "searchers":
                return new Searchers()
            case "zoom":
                return new ZoomSearcher(attributes.get(NAME), attributes.get("baseUrl"), asyncHttpClient)
            case "webhose":
                return new WebhoseSearcher(attributes.get(NAME), attributes.get("key"), asyncHttpClient)
            case "github":
                return new GithubSearcher(attributes.get(NAME), attributes.get("repo"), asyncHttpClient)
            case "fred":
                return new FredSearcher(attributes.get(NAME), attributes.get("serverUrl"), asyncHttpClient)
            case "duckduckgo":
                return new DuckDuckGoSearcher(attributes.get(NAME), asyncHttpClient)
            default:
                return null
        }
    }
}