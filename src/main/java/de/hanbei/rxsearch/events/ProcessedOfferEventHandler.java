package de.hanbei.rxsearch.events;

import de.hanbei.rxsearch.filter.ProcessedHandler;
import de.hanbei.rxsearch.model.Offer;
import io.vertx.core.eventbus.EventBus;

import java.util.List;

public class ProcessedOfferEventHandler implements ProcessedHandler {

    private final EventBus eventBus;

    public ProcessedOfferEventHandler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void offersFiltered(String requestId, String processorName, boolean filter, List<Offer> remainingOffers) {
        eventBus.publish(OfferProcessedEvent.topic(), new OfferProcessedEvent(requestId, processorName, filter, remainingOffers));
    }
}
