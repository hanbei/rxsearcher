package rxsearch.config

import rxsearch.filter.impl.NoImageFilter
import rxsearch.searcher.duckduckgo.DuckDuckGoSearcher
import rxsearch.searcher.github.GithubSearcher
import rxsearch.searcher.webhose.WebhoseSearcher
import spock.lang.Specification

class ConfigurationBuilderTest extends Specification {

    def searcherConfig = new ConfigurationBuilder(null)

    def "load configuration for name and env test searchers"() {
        when:
        Configuration config = searcherConfig.loadConfiguration("test_app", "staging", "de")
        def searcher = config.searcher()
        then:
        searcher.size() == 3
        searcher.get(0) instanceof WebhoseSearcher
        searcher.get(1) instanceof GithubSearcher
        searcher.get(2) instanceof DuckDuckGoSearcher
    }

    def "load configuration for name and env test filters"() {
        when:
        Configuration config = searcherConfig.loadConfiguration("test_app", "staging", "de")
        def filter = config.filter()
        then:
        filter.size() == 1
        filter.get(0) instanceof NoImageFilter
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
