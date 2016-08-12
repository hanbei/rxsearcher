package de.hanbei.rxsearch.filter;

import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import rx.Observable;

import java.util.ArrayList;

public class FilterCoordinator {

    private final ArrayList<Filter> filters;

    public FilterCoordinator(ArrayList<Filter> filters) {
        this.filters = filters;
    }

    public Observable<Offer> filter(Query query, Observable<Offer> offerObservable) {
        for (Filter filter : filters) {
            offerObservable = filter.filter(query, offerObservable);
        }
        return offerObservable;
    }
}
