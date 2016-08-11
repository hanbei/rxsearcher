package de.hanbei.rxsearch.filter;

import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import rx.Observable;

/**
 * Created by hanbei on 8/11/16.
 */
public interface Filter {
    Observable<Offer> filter(Query query, Observable<Offer> observable);
}
