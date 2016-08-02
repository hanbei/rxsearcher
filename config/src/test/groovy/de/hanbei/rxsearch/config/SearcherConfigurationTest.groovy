package de.hanbei.rxsearch.config

import de.hanbei.rxsearch.searcher.Searcher
import de.hanbei.rxsearch.searcher.duckduckgo.DuckDuckGoSearcher
import de.hanbei.rxsearch.searcher.fred.FredSearcher
import de.hanbei.rxsearch.searcher.github.GithubSearcher
import de.hanbei.rxsearch.searcher.webhose.WebhoseSearcher
import de.hanbei.rxsearch.searcher.zoom.ZoomSearcher
import spock.lang.Specification

class SearcherConfigurationTest extends Specification {

    def searcherConfig = new SearcherConfiguration(null)

    def "load configuration for name and env"() {
        when:
        List<Searcher> searcher = searcherConfig.loadConfiguration("test_app", "staging", "de")
        then:
        searcher.size() == 5
        searcher.get(0) instanceof ZoomSearcher
        searcher.get(1) instanceof WebhoseSearcher
        searcher.get(2) instanceof GithubSearcher
        searcher.get(3) instanceof FredSearcher
        searcher.get(4) instanceof DuckDuckGoSearcher
    }

    def "load not existing configuration"() {
        when:
        List<Searcher> searcher = searcherConfig.loadConfiguration("test2", "bla", "foo")
        then:
        searcher.size() == 5
        searcher.get(0) instanceof ZoomSearcher
        searcher.get(1) instanceof WebhoseSearcher
        searcher.get(2) instanceof GithubSearcher
        searcher.get(3) instanceof FredSearcher
        searcher.get(4) instanceof DuckDuckGoSearcher
    }
}
