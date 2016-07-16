package de.hanbei.rxsearch.coordination;

import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.Searcher;
import rx.Observable;

import java.util.List;

public class SearchCoordinator {

    private final List<Searcher> searcher;

    public SearchCoordinator(List<Searcher> searcher) {
        this.searcher = searcher;
    }

    public void startSearch(Query query, ResponseHandler handler) {
        Observable.from(searcher)
                .flatMap(searcher -> searcher.search(query)).toList().subscribe(
                handler::handleSuccess,
                handler::handleError
        );
    }
}
