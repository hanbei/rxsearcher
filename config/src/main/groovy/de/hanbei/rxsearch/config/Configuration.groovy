package de.hanbei.rxsearch.config

import de.hanbei.rxsearch.filter.OfferFilter
import de.hanbei.rxsearch.searcher.Searcher

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

    List<OfferFilter> filter() {
        return filter.filters;
    }

}
