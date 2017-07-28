package rxsearch.filter;

import rxsearch.model.Hit;
import rxsearch.model.Query;
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
