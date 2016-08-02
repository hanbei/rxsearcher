package de.hanbei.rxsearch.config

import com.ning.http.client.AsyncHttpClient
import de.hanbei.rxsearch.searcher.duckduckgo.DuckDuckGoSearcher
import de.hanbei.rxsearch.searcher.fred.FredSearcher
import de.hanbei.rxsearch.searcher.github.GithubSearcher
import de.hanbei.rxsearch.searcher.webhose.WebhoseSearcher
import de.hanbei.rxsearch.searcher.zoom.ZoomRequestBuilder
import de.hanbei.rxsearch.searcher.zoom.ZoomSearcher
import spock.lang.Specification

class SearchConfigBuilderTest extends Specification {

    def searchConfigBuilder = new SearchConfigBuilder(Mock(AsyncHttpClient))

    def "create searcher list from dsl"() {
        when: "searcher de builder builds searcher list"

        def searchers = searchConfigBuilder.searchers() {
            zoom(name: "zoom", baseUrl: "http://example.com/zoom")
            webhose(name: "webhose", key: "71asda23")
            github(name: "github", repo: "hanbei/mockhttpserver")
            fred(name: "fred", serverUrl: "hanbei/mockhttpserver")
            duckduckgo(name: "ddgo")
        }

        then: "searchers should contain correctly instantiated searchers"
        def innerSearchers = searchers.searchers
        innerSearchers.size == 5
        innerSearchers.get(0) instanceof ZoomSearcher
        innerSearchers.get(1) instanceof WebhoseSearcher
        innerSearchers.get(2) instanceof GithubSearcher
        innerSearchers.get(3) instanceof FredSearcher
        innerSearchers.get(4) instanceof DuckDuckGoSearcher
    }

    def "create zoom searcher"() {
        when:
        ZoomSearcher searcher = searchConfigBuilder.zoom([name: "zoom", baseUrl: "http://example.com/zoom"])

        then:
        searcher.name == "zoom"
    }
}
