package de.hanbei.rxsearch.filter;

import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.model.User;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class OfferProcessorCoordinatorTest {

    private Query query;
    private List<Offer> offers;

    @Before
    public void setup() {
        query = Query.builder().keywords("keywords").requestId("requestId").country("de").user(User.getDefaultUser()).build();
        offers = newArrayList(
                Offer.builder().url("url1").title("title1").price(1.0, "USD").searcher("searcher1").build(),
                Offer.builder().url("url2").title("title2").price(2.0, "USD").searcher("searcher2").build(),
                Offer.builder().url("url3").title("title3").price(3.0, "USD").searcher("searcher3").build()
        );
    }

    @Test
    public void coordinatorLogsFilteredOffers() throws Exception {
        OfferFilter offerFilter1 = (q, offerObservable) -> offerObservable.filter(offer -> offer.getPrice().getAmount() > 1.0);
        OfferFilter offerFilter2 = (q, offerObservable) -> offerObservable.filter(offer -> offer.getPrice().getAmount() > 2.0);

        ProcessedHandler processedHandler = mock(ProcessedHandler.class);

        OfferProcessorCoordinator coordinator = new OfferProcessorCoordinator(newArrayList(
                offerFilter1,
                offerFilter2
        ), processedHandler);

        TestObserver<Offer> observer = new TestObserver<>();
        coordinator.filter(query, Observable.fromIterable(offers)).subscribe(observer);
        observer.assertComplete();
        observer.assertResult(offers.get(2));
        verify(processedHandler, times(1)).offersFiltered("requestId", offerFilter1.getClass().getSimpleName(), true, singletonList(offers.get(0)));
        verify(processedHandler, times(1)).offersFiltered("requestId", offerFilter2.getClass().getSimpleName(), true, singletonList(offers.get(1)));
    }

    @Test
    public void coordinatorLogsAllOffersFromProcessor() throws Exception {
        OfferProcessor offerProcessor = (q, offerObservable) -> offerObservable;

        ProcessedHandler processedHandler = mock(ProcessedHandler.class);

        OfferProcessorCoordinator coordinator = new OfferProcessorCoordinator(newArrayList(
                offerProcessor), processedHandler);

        TestObserver<Offer> observer = new TestObserver<>();
        coordinator.filter(query, Observable.fromIterable(offers)).subscribe(observer);
        observer.assertComplete();
        observer.assertResult(offers.get(0), offers.get(1), offers.get(2));
        verify(processedHandler, times(1)).offersFiltered("requestId", offerProcessor.getClass().getSimpleName(), false, offers);
    }


    @Test
    public void coordinatorLogsFiltersAndProcessor() throws Exception {
        OfferFilter filter = (q, offerObservable) -> offerObservable.filter(offer -> offer.getPrice().getAmount() > 1.0);
        OfferProcessor processor = (q, offerObservable) -> offerObservable;

        ProcessedHandler processedHandler = mock(ProcessedHandler.class);

        OfferProcessorCoordinator coordinator = new OfferProcessorCoordinator(newArrayList(
                filter,
                processor
        ), processedHandler);

        TestObserver<Offer> observer = new TestObserver<>();
        coordinator.filter(query, Observable.fromIterable(offers)).subscribe(observer);
        observer.assertComplete();
        observer.assertResult(offers.get(1), offers.get(2));
        verify(processedHandler, times(1)).offersFiltered("requestId", filter.getClass().getSimpleName(), true, singletonList(offers.get(0)));
        verify(processedHandler, times(1)).offersFiltered("requestId", processor.getClass().getSimpleName(), false, newArrayList(offers.get(1), offers.get(2)));
    }

}