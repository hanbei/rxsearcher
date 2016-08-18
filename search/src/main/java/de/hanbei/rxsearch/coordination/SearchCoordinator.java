package de.hanbei.rxsearch.coordination;

import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.Searcher;
import de.hanbei.rxsearch.searcher.SearcherException;
import rx.Observable;

import java.util.List;

public class SearchCoordinator {

    private final List<Searcher> searcher;

    public SearchCoordinator(List<Searcher> searcher) {
        this.searcher = searcher;
    }

    public Observable<Offer> startSearch(Query query, SearcherErrorHandler handler) {
        return Observable.from(searcher)
                .flatMap(
                        searcher -> searcher.search(query)
                                .onErrorResumeNext(t -> handler.searcherError(SearcherException.wrap(query, t))
                                )
                );
    }
}
