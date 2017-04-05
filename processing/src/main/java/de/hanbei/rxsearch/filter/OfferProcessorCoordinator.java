package de.hanbei.rxsearch.filter;

import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import io.reactivex.Observable;

import java.util.List;

public class OfferProcessorCoordinator {

    private final List<OfferProcessor> offerProcessors;

    public OfferProcessorCoordinator(List<OfferProcessor> offerProcessors) {
        this.offerProcessors = offerProcessors;
    }

    public Observable<Offer> filter(Query query, Observable<Offer> offerObservable) {
        for (OfferProcessor processor : offerProcessors) {
            offerObservable = processor.process(query, offerObservable);
        }
        return offerObservable;
    }
}
