package rxsearch.config.search

import rxsearch.model.Hit
import rxsearch.model.Query
import rxsearch.searcher.Searcher
import io.reactivex.Observable
import rxsearch.config.SearcherConfiguration
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
            Observable<Hit> search(Query query) {
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
