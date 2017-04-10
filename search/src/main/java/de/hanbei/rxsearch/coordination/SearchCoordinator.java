package de.hanbei.rxsearch.coordination;

import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.Searcher;
import de.hanbei.rxsearch.searcher.SearcherException;
import io.reactivex.Observable;

import java.util.List;

public class SearchCoordinator {

    private final List<Searcher> searchers;
    private final SearcherErrorHandler onError;
    private final SearcherCompletionHandler onCompleted;
    private final SearcherResult onNext;

    public SearchCoordinator(List<Searcher> searcher) {
        this(searcher, SearchCoordinator::noop, SearchCoordinator::noop, SearchCoordinator::noop);
    }

    public SearchCoordinator(List<Searcher> searcher, SearcherErrorHandler onError) {
        this(searcher, onError, SearchCoordinator::noop, SearchCoordinator::noop);
    }

    public SearchCoordinator(List<Searcher> searcher, SearcherErrorHandler onError, SearcherCompletionHandler onCompleted) {
        this(searcher, onError, onCompleted, SearchCoordinator::noop);
    }

    public SearchCoordinator(List<Searcher> searcher, SearcherErrorHandler onError, SearcherCompletionHandler onCompleted, SearcherResult resultHandler) {
        this.searchers = searcher;
        this.onError = onError;
        this.onCompleted = onCompleted;
        this.onNext = resultHandler;
    }

    public Observable<Offer> startSearch(Query query) {
        return Observable.fromIterable(searchers)
                .flatMap(
                        searcher -> searcher.search(query)
                                .doOnNext(offer -> onNext.searcherResult(searcher.getName(), offer))
                                .doOnComplete(() -> onCompleted.searcherCompleted(searcher.getName(), query))
                                .onErrorResumeNext(Observable.empty())
                                .doOnError(t -> onError.searcherError(searcher.getName(), SearcherException.wrap(t).searcher(searcher.getName()).query(query)))
                ).map(offer -> Offer.from(offer).requestId(query.getRequestId()).build());
    }

    private static <T> void noop(String s, T t) {
    }
}
