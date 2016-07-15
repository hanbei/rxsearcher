package de.hanbei.rxsearch.searcher;

import de.hanbei.rxsearch.model.Offer;
import rx.Observable;

/**
 * Created by hanbei on 4/17/16.
 */
public interface Searcher {
    String getName();

    Observable<Offer> search(String searchInput);
}
