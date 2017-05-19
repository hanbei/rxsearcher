package de.hanbei.rxsearch.config

import de.hanbei.rxsearch.filter.impl.PriceFilter
import de.hanbei.rxsearch.searcher.duckduckgo.DuckDuckGoSearcher
import de.hanbei.rxsearch.searcher.fred.FredSearcher
import de.hanbei.rxsearch.searcher.github.GithubSearcher
import de.hanbei.rxsearch.searcher.webhose.WebhoseSearcher
import de.hanbei.rxsearch.searcher.zoom.ZoomSearcher
import spock.lang.Specification

class ConfigurationBuilderTest extends Specification {

    def searcherConfig = new ConfigurationBuilder(null)

    def "load configuration for name and env test searchers"() {
        when:
        Configuration config = searcherConfig.loadConfiguration("test_app", "staging", "de")
        def searcher = config.searcher()
        then:
        searcher.size() == 5
        searcher.get(0) instanceof ZoomSearcher
        searcher.get(1) instanceof WebhoseSearcher
        searcher.get(2) instanceof GithubSearcher
        searcher.get(3) instanceof FredSearcher
        searcher.get(4) instanceof DuckDuckGoSearcher
    }

    def "load configuration for name and env test filters"() {
        when:
        Configuration config = searcherConfig.loadConfiguration("test_app", "staging", "de")
        def filter = config.filter()
        then:
        filter.size() == 1
        filter.get(0) instanceof PriceFilter
    }


    def "load not existing configuration"() {
        when:
        searcherConfig.loadConfiguration("test2", "bla", "foo")
        then:
        def e = thrown(ConfigurationException)
        e.country == "foo"
        e.environment == "bla"
        e.appName == "test2"
    }

    def "load broken configuration"() {
        when:
        searcherConfig.loadConfiguration("test", "staging", "broken")
        then:
        def e = thrown(ConfigurationException)
        e.country == "broken"
        e.environment == "staging"
        e.appName == "test"
    }
}
