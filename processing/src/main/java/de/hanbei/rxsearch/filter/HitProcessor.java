package de.hanbei.rxsearch.filter;

import de.hanbei.rxsearch.model.Hit;
import de.hanbei.rxsearch.model.Query;
import io.reactivex.Observable;

public interface HitProcessor {

    Observable<Hit> process(Query query, Observable<Hit> observable);

    default boolean filters() {
        return false;
    }

}
