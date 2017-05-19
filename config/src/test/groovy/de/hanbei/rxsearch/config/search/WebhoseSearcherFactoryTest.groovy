package de.hanbei.rxsearch.config.search

import de.hanbei.rxsearch.config.ConfigurationException
import de.hanbei.rxsearch.searcher.webhose.WebhoseSearcher

class WebhoseSearcherFactoryTest extends SearcherNodeSpecification {

    void setup() {
        searcherFactory = new WebhoseSearcherFactory()
    }

    def "returns new instance"() {
        when:
        WebhoseSearcher instance = searcherFactory.newInstance(null, null, null, [name: "fred", key: "url"]) as WebhoseSearcher
        then:
        instance instanceof WebhoseSearcher
        instance.getName() == "fred"
    }

    def "missing name throws"() {
        when:
        searcherFactory.newInstance(null, null, null, [key: "url"])
        then:
        def e = thrown(ConfigurationException)
        e.getMessage() == "Missing config value 'name' for 'WebhoseSearcher'"
    }

    def "missing key throws"() {
        when:
        searcherFactory.newInstance(null, null, null, [name: "fred"])
        then:
        def e = thrown(ConfigurationException)
        e.getMessage() == "Missing config value 'key' for 'WebhoseSearcher'"
    }
}
