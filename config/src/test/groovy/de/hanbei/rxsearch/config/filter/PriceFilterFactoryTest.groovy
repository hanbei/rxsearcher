package de.hanbei.rxsearch.config.filter

import de.hanbei.rxsearch.filter.impl.PriceFilter

class PriceFilterFactoryTest extends FilterNodeSpecification {

    void setup() {
        filterFactory = new PriceFilterFactory()
    }

    def "returns new price filter"() {
        when:
        def instance = filterFactory.newInstance(null, null, null, null)
        then:
        instance instanceof PriceFilter
    }

}
