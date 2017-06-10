package de.hanbei.rxsearch.filter;

import de.hanbei.rxsearch.model.Hit;
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

public class HitProcessorCoordinatorTest {

    private Query query;
    private List<Hit> hits;

    @Before
    public void setup() {
        query = Query.builder().keywords("keywords").requestId("requestId").country("de").user(User.getDefaultUser()).build();
        hits = newArrayList(
                Hit.builder().url("url1").title("title1").searcher("searcher1").build(),
                Hit.builder().url("url2").title("title2").searcher("searcher2").build(),
                Hit.builder().url("url3").title("title3").searcher("searcher3").build()
        );
    }

    @Test
    public void coordinatorLogsFilteredOffers() throws Exception {
        HitFilter hitFilter1 = (q, offerObservable) -> offerObservable.filter(offer -> offer.getSearcher().equals("searcher1"));
        HitFilter hitFilter2 = (q, offerObservable) -> offerObservable.filter(offer -> offer.getSearcher().equals("searcher2"));

        ProcessedHandler processedHandler = mock(ProcessedHandler.class);

        HitProcessorCoordinator coordinator = new HitProcessorCoordinator(newArrayList(
                hitFilter1,
                hitFilter2
        ), processedHandler);

        TestObserver<Hit> observer = new TestObserver<>();
        coordinator.filter(query, Observable.fromIterable(hits)).subscribe(observer);
        observer.assertComplete();
        observer.assertResult(hits.get(2));
        verify(processedHandler, times(1)).offersFiltered("requestId", hitFilter1.getClass().getSimpleName(), true, singletonList(hits.get(0)));
        verify(processedHandler, times(1)).offersFiltered("requestId", hitFilter2.getClass().getSimpleName(), true, singletonList(hits.get(1)));
    }

    @Test
    public void coordinatorLogsAllOffersFromProcessor() throws Exception {
        HitProcessor hitProcessor = (q, offerObservable) -> offerObservable;

        ProcessedHandler processedHandler = mock(ProcessedHandler.class);

        HitProcessorCoordinator coordinator = new HitProcessorCoordinator(newArrayList(
                hitProcessor), processedHandler);

        TestObserver<Hit> observer = new TestObserver<>();
        coordinator.filter(query, Observable.fromIterable(hits)).subscribe(observer);
        observer.assertComplete();
        observer.assertResult(hits.get(0), hits.get(1), hits.get(2));
        verify(processedHandler, times(1)).offersFiltered("requestId", hitProcessor.getClass().getSimpleName(), false, hits);
    }


    @Test
    public void coordinatorLogsFiltersAndProcessor() throws Exception {
        HitFilter filter = (q, offerObservable) -> offerObservable.filter(offer -> offer.getSearcher().equals("searcher1"));
        HitProcessor processor = (q, offerObservable) -> offerObservable;

        ProcessedHandler processedHandler = mock(ProcessedHandler.class);

        HitProcessorCoordinator coordinator = new HitProcessorCoordinator(newArrayList(
                filter,
                processor
        ), processedHandler);

        TestObserver<Hit> observer = new TestObserver<>();
        coordinator.filter(query, Observable.fromIterable(hits)).subscribe(observer);
        observer.assertComplete();
        observer.assertResult(hits.get(1), hits.get(2));
        verify(processedHandler, times(1)).offersFiltered("requestId", filter.getClass().getSimpleName(), true, singletonList(hits.get(0)));
        verify(processedHandler, times(1)).offersFiltered("requestId", processor.getClass().getSimpleName(), false, newArrayList(hits.get(1), hits.get(2)));
    }

}