package rxsearch.filter;

import rxsearch.model.Hit;
import rxsearch.model.Query;
import io.reactivex.Observable;

import java.util.ArrayList;
import java.util.List;

public class HitProcessorCoordinator {

    private final List<HitProcessor> hitProcessors;
    private final ProcessedHandler processedHandler;

    public HitProcessorCoordinator(List<HitProcessor> hitProcessors) {
        this(hitProcessors, (r, n, f, l) -> {
        });
    }

    public HitProcessorCoordinator(List<HitProcessor> hitProcessors, ProcessedHandler processedHandler) {
        this.hitProcessors = hitProcessors;
        this.processedHandler = processedHandler;
    }

    public Observable<Hit> filter(Query query, Observable<Hit> offerObservable) {
        for (HitProcessor processor : hitProcessors) {
            FilterLogger filterLogger = new FilterLogger(query.getRequestId(), processor);
            offerObservable = processor.process(query,
                    offerObservable.doOnNext(filterLogger::record)
            ).doOnNext(filterLogger::remove).doOnComplete(filterLogger::onComplete);
        }
        return offerObservable;
    }

    private class FilterLogger {

        private final List<Hit> filteredHits;
        private final String requestId;
        private final HitProcessor processor;

        FilterLogger(String requestId, HitProcessor processor) {
            this.requestId = requestId;
            this.processor = processor;
            filteredHits = new ArrayList<>();
        }

        void record(Hit hit) {
            filteredHits.add(hit);
        }

        void remove(Hit hit) {
            if (processor.filters()) {
                filteredHits.remove(hit);
            }
        }

        void onComplete() {
            String name = processor.getClass().getSimpleName();
            processedHandler.offersFiltered(requestId, name, processor.filters(), filteredHits);
        }

    }
}
