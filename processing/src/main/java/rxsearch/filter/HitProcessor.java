package rxsearch.filter;

import rxsearch.model.Hit;
import rxsearch.model.Query;
import io.reactivex.Observable;

public interface HitProcessor {

    Observable<Hit> process(Query query, Observable<Hit> observable);

    default boolean filters() {
        return false;
    }

}
