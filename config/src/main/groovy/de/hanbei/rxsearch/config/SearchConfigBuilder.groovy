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
    private static final String REPO = "repo"
    private static final String KEY = "key"
    private static final String BASE_URL = "baseUrl"

    private static final String DUCKDUCKGO = "duckduckgo"
    private static final String FRED = "fred"
    private static final String GITHUB = "github"
    private static final String WEBHOSE = "webhose"
    private static final String ZOOM = "zoom"
    private static final String SEARCHERS = "searchers"

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
            case SEARCHERS:
                return new Searchers()
            case ZOOM:
                return createZoomSearcher(attributes)
            case WEBHOSE:
                return createWebhoseSearcher(attributes)
            case GITHUB:
                return createGithubSearcher(attributes)
            case FRED:
                return createFredSearcher(attributes)
            case DUCKDUCKGO:
                return createDuckDuckGoSearcher(attributes)
            default:
                throw new ConfigurationException("Encountered unknown searcher: ${name}")
        }
    }

    public Object duckduckgo(String name) {
        createNode(DUCKDUCKGO, [name: name])
    }

    public Object fred(String name, String baseUrl) {
        createNode(FRED, [name: name, baseUrl: baseUrl])
    }

    public Object github(String name, String repo) {
        createNode(GITHUB, [name: name, repo: repo])
    }

    public Object webhose(String name, String key) {
        createNode(WEBHOSE, [name: name, key: key])
    }

    public Object zoom(String name, String baseUrl) {
        createNode(ZOOM, [name: name, baseUrl: baseUrl])
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
        checkAttributes("GithubSearcher", attributes, NAME, REPO)
        return new GithubSearcher(attributes.get(NAME) as String, attributes.get(REPO) as String, asyncHttpClient)
    }

    private WebhoseSearcher createWebhoseSearcher(Map attributes) {
        checkAttributes("WebhoseSearcher", attributes, NAME, KEY)
        return new WebhoseSearcher(attributes.get(NAME) as String, attributes.get(KEY) as String, asyncHttpClient)
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