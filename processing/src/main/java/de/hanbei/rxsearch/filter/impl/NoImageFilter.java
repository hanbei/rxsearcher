package de.hanbei.rxsearch.filter.impl;

import de.hanbei.rxsearch.filter.HitFilter;
import de.hanbei.rxsearch.model.Hit;
import de.hanbei.rxsearch.model.Query;
import io.reactivex.Observable;

public class NoImageFilter implements HitFilter {

    @Override
    public Observable<Hit> filter(Query query, Observable<Hit> observable) {
        return observable.filter(hit -> hit.getImage() != null);
    }
}
