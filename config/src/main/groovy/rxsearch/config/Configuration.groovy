package rxsearch.config

import rxsearch.filter.HitFilter
import rxsearch.searcher.Searcher

class Configuration {

    final String country

    private SearcherConfiguration searcher
    private FilterConfiguration filter

    Configuration(String country) {
        this.country = country
    }

    List<Searcher> searcher() {
        return searcher.searchers;
    }

    List<HitFilter> filter() {
        return filter.filters;
    }

}
