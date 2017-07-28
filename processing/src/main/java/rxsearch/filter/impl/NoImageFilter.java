package rxsearch.filter.impl;

import rxsearch.filter.HitFilter;
import rxsearch.model.Hit;
import rxsearch.model.Query;
import io.reactivex.Observable;

public class NoImageFilter implements HitFilter {

    @Override
    public Observable<Hit> filter(Query query, Observable<Hit> observable) {
        return observable.filter(hit -> hit.getImage() != null);
    }
}
