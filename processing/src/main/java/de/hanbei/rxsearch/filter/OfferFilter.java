package de.hanbei.rxsearch.filter;

import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import io.reactivex.Observable;

public interface OfferFilter extends OfferProcessor {

    @Override
    default Observable<Offer> process(Query query, Observable<Offer> observable) {
        return filter(query, observable);
    }

    @Override
    default boolean filters() {
        return true;
    }

    Observable<Offer> filter(Query query, Observable<Offer> observable);

}
