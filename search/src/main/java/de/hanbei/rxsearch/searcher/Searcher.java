package de.hanbei.rxsearch.searcher;

import de.hanbei.rxsearch.metrics.Measured;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import io.reactivex.Observable;

public interface Searcher extends Measured {
    String getName();

    Observable<Offer> search(Query query);
}
