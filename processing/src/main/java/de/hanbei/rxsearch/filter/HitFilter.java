package de.hanbei.rxsearch.filter;

import de.hanbei.rxsearch.model.Hit;
import de.hanbei.rxsearch.model.Query;
import io.reactivex.Observable;

public interface HitFilter extends HitProcessor {

    @Override
    default Observable<Hit> process(Query query, Observable<Hit> observable) {
        return filter(query, observable);
    }

    @Override
    default boolean filters() {
        return true;
    }

    Observable<Hit> filter(Query query, Observable<Hit> observable);

}
