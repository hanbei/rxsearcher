package de.hanbei.rxsearch.filter.impl;

import de.hanbei.rxsearch.filter.Filter;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import rx.Observable;

public class PriceFilter implements Filter {

    @Override
    public Observable<Offer> filter(Query query, Observable<Offer> observable) {
        if (query.price() == null) {
            return observable;
        }

        return observable.filter(offer -> query.price().getAmount() * 0.5 <= offer.getPrice().getAmount() &&
                offer.getPrice().getAmount() <= query.price().getAmount() * 2);
    }
}
