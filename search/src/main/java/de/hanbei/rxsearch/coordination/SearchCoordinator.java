package de.hanbei.rxsearch.coordination;

import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.Searcher;
import de.hanbei.rxsearch.searcher.SearcherException;
import rx.Observable;

import java.util.List;

public class SearchCoordinator {

    private final List<Searcher> searchers;
    private final SearcherErrorHandler onError;
    private final SearcherCompletionHandler onCompleted;

    public SearchCoordinator(List<Searcher> searcher) {
        this(searcher, t -> Observable.empty(), SearchCoordinator::completionNoop);
    }

    public SearchCoordinator(List<Searcher> searcher, SearcherErrorHandler onError) {
        this(searcher, onError, SearchCoordinator::completionNoop);
    }

    public SearchCoordinator(List<Searcher> searcher, SearcherErrorHandler onError, SearcherCompletionHandler onCompleted) {
        this.searchers = searcher;
        this.onError = onError;
        this.onCompleted = onCompleted;
    }

    public Observable<Offer> startSearch(Query query) {
        return Observable.from(searchers)
                .flatMap(
                        searcher -> searcher.search(query)
                                .onErrorResumeNext(t ->
                                        onError.searcherError(SearcherException.wrap(t).searcher(searcher.getName()).query(query))
                                )
                                .doOnCompleted(() -> onCompleted.searcherCompleted(searcher.getName(), query))
                );
    }

    private static void completionNoop(String s, Query q) {
    }
}
