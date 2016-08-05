package de.hanbei.rxsearch.config

import com.ning.http.client.AsyncHttpClient
import de.hanbei.rxsearch.searcher.Searcher
import de.hanbei.rxsearch.searcher.duckduckgo.DuckDuckGoSearcher
import de.hanbei.rxsearch.searcher.fred.FredSearcher
import de.hanbei.rxsearch.searcher.github.GithubSearcher
import de.hanbei.rxsearch.searcher.webhose.WebhoseSearcher
import de.hanbei.rxsearch.searcher.zoom.ZoomSearcher

class SearchConfigBuilder extends BuilderSupport {

    private static final String NAME = "name"
    public static final String BASE_URL = "baseUrl"
    private AsyncHttpClient asyncHttpClient

    SearchConfigBuilder(AsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient
    }

    @Override
    protected void setParent(Object parent, Object child) {
        if (parent instanceof Searchers) {
            ((Searchers) parent).searchers.add((Searcher) child)
        } else {
            throw new ConfigurationException("${child} wrapped in parent object that is not a Searcher")
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
                return createZoomSearcher(attributes)
            case "webhose":
                return createWebhoseSearcher(attributes)
            case "github":
                return createGithubSearcher(attributes)
            case "fred":
                return createFredSearcher(attributes)
            case "duckduckgo":
                return createDuckDuckGoSearcher(attributes)
            default:
                throw new ConfigurationException("Encountered unknown searcher: ${name}")
        }
    }

    private DuckDuckGoSearcher createDuckDuckGoSearcher(Map attributes) {
        checkAttributes("DuckDuckGoSearcher", attributes, NAME)
        return new DuckDuckGoSearcher(attributes.get(NAME) as String, asyncHttpClient)
    }

    private FredSearcher createFredSearcher(Map attributes) {
        checkAttributes("FredSearcher", attributes, NAME, BASE_URL)
        return new FredSearcher(attributes.get(NAME) as String, attributes.get(BASE_URL) as String, asyncHttpClient)
    }

    private GithubSearcher createGithubSearcher(Map attributes) {
        checkAttributes("GithubSearcher", attributes, NAME, "repo")
        return new GithubSearcher(attributes.get(NAME) as String, attributes.get("repo") as String, asyncHttpClient)
    }

    private WebhoseSearcher createWebhoseSearcher(Map attributes) {
        checkAttributes("WebhoseSearcher", attributes, NAME, "key")
        return new WebhoseSearcher(attributes.get(NAME) as String, attributes.get("key") as String, asyncHttpClient)
    }

    private ZoomSearcher createZoomSearcher(Map attributes) {
        checkAttributes("ZoomSearcher", attributes, NAME, BASE_URL)
        return new ZoomSearcher(attributes.get(NAME) as String, attributes.get(BASE_URL) as String, asyncHttpClient)
    }

    static def checkAttributes(String searcherName, Map attributes, String... attributesToCheck) {
        for (attr in attributesToCheck) {
            if (!attributes.containsKey(attr)) {
                throw new ConfigurationException("Missing config value '${attr}' for '${searcherName}'")
            }
        }

    }
}