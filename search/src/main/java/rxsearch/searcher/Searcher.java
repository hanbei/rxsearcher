package rxsearch.searcher;

import rxsearch.metrics.Measured;
import rxsearch.model.Hit;
import rxsearch.model.Query;
import io.reactivex.Observable;

public interface Searcher extends Measured {
    String getName();

    Observable<Hit> search(Query query);
}
