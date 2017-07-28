package rxsearch.config.search

import rxsearch.config.ConfigurationException
import rxsearch.searcher.github.GithubSearcher

class GithubSearcherFactoryTest extends SearcherNodeSpecification {

    void setup() {
        searcherFactory = new GithubSearcherFactory()
    }

    def "returns new instance"() {
        when:
        GithubSearcher instance = searcherFactory.newInstance(null, null, null, [name: "fred", repo: "url"]) as GithubSearcher
        then:
        instance instanceof GithubSearcher
        instance.getName() == "fred"
    }

    def "missing name throws"() {
        when:
        searcherFactory.newInstance(null, null, null, [repo: "url"])
        then:
        def e = thrown(ConfigurationException)
        e.getMessage() == "Missing config value 'name' for 'GithubSearcher'"
    }

    def "missing baseurl throws"() {
        when:
        searcherFactory.newInstance(null, null, null, [name: "fred"])
        then:
        def e = thrown(ConfigurationException)
        e.getMessage() == "Missing config value 'repo' for 'GithubSearcher'"
    }
}
