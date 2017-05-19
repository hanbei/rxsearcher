package de.hanbei.rxsearch.config.search

import de.hanbei.rxsearch.config.SearcherConfiguration
import de.hanbei.rxsearch.config.search.AbstractSearcherFactory
import de.hanbei.rxsearch.model.Offer
import de.hanbei.rxsearch.model.Query
import de.hanbei.rxsearch.searcher.Searcher
import io.reactivex.Observable
import spock.lang.Specification

abstract class SearcherNodeSpecification extends Specification {

    protected AbstractSearcherFactory searcherFactory

    def "doesn't handle node attributes"() {
        when:
        def attribute = searcherFactory.onHandleNodeAttributes(null, null, null)
        then:
        attribute == false

    }

    def "should be a leaf"() {
        when:
        def isLeaf = searcherFactory.isLeaf()
        then:
        isLeaf == true
    }

    def "adds searcher to parent"() {
        given:
        def searcherConfig = new SearcherConfiguration()
        def searcher = new Searcher() {
            @Override
            String getName() {
                return null
            }

            @Override
            Observable<Offer> search(Query query) {
                return null
            }
        }
        when:
        searcherFactory.setParent(null, searcherConfig, searcher)
        then:
        searcherConfig.searchers.size() == 1
        searcherConfig.searchers.get(0) == searcher
    }


}
