package de.hanbei.rxsearch.filter;

import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import io.reactivex.Observable;

import java.util.ArrayList;
import java.util.List;

public class OfferProcessorCoordinator {

    private final List<OfferProcessor> offerProcessors;
    private final ProcessedHandler processedHandler;

    public OfferProcessorCoordinator(List<OfferProcessor> offerProcessors) {
        this(offerProcessors, (r, n, f, l) -> {
        });
    }

    public OfferProcessorCoordinator(List<OfferProcessor> offerProcessors, ProcessedHandler processedHandler) {
        this.offerProcessors = offerProcessors;
        this.processedHandler = processedHandler;
    }

    public Observable<Offer> filter(Query query, Observable<Offer> offerObservable) {
        for (OfferProcessor processor : offerProcessors) {
            FilterLogger filterLogger = new FilterLogger(query.getRequestId(), processor);
            offerObservable = processor.process(query,
                    offerObservable.doOnNext(filterLogger::record)
            ).doOnNext(filterLogger::remove).doOnComplete(filterLogger::onComplete);
        }
        return offerObservable;
    }

    private class FilterLogger {

        private final List<Offer> filteredOffers;
        private String requestId;
        private final OfferProcessor processor;
        private final boolean isFilter;

        FilterLogger(String requestId, OfferProcessor processor) {
            this.requestId = requestId;
            this.processor = processor;
            isFilter = processor instanceof OfferFilter;
            filteredOffers = new ArrayList<>();
        }

        void record(Offer offer) {
            filteredOffers.add(offer);
        }

        void remove(Offer offer) {
            if (isFilter) {
                filteredOffers.remove(offer);
            }
        }

        void onComplete() {
            String name = processor.getClass().getSimpleName();
            processedHandler.offersFiltered(requestId, name, isFilter, filteredOffers);
        }

    }
}
