package rxsearch.config.filter

import io.reactivex.Observable
import rxsearch.config.FilterConfiguration
import rxsearch.filter.HitFilter
import rxsearch.model.Hit
import rxsearch.model.Query
import spock.lang.Specification

abstract class FilterNodeSpecification extends Specification {

    protected AbstractFilterFactory filterFactory

    def "doesn't handle node attributes"() {
        when:
        def attribute = filterFactory.onHandleNodeAttributes(null, null, null)
        then:
        attribute == false

    }

    def "should be a leaf"() {
        when:
        def isLeaf = filterFactory.isLeaf()
        then:
        isLeaf == true
    }

    def "adds to filter parent"() {
        given:
        def filterConfig = new FilterConfiguration()
        def filter = new HitFilter() {
            @Override
            Observable<Hit> filter(Query query, Observable<Hit> observable) {
                return null
            }
        }
        when:
        filterFactory.setParent(null, filterConfig, filter)
        then:
        filterConfig.filters.size() == 1
        filterConfig.filters.get(0) == filter
    }


}
