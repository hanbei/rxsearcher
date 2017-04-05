package de.hanbei.rxsearch.filter.impl;

import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class PriceFilterTest {

    private static final String CURRENCY = "EUR";
    private PriceFilter filter;
    private Query query;
    private Offer[] offers;
    private TestScheduler scheduler;
    private PublishSubject<Offer> publishSubject;

    @Before
    public void setup() {
        scheduler = new TestScheduler();
        publishSubject = PublishSubject.create();

        filter = new PriceFilter();
        query = Query.builder().keywords("keywords").requestId("requestId").country("de").price(20.0, CURRENCY).build();
        offers = new Offer[]{
                Offer.builder().url("").title("").price(35.0, CURRENCY).searcher("").build(),
                Offer.builder().url("").title("").price(41.0, CURRENCY).searcher("").build(),
                Offer.builder().url("").title("").price(40.0, CURRENCY).searcher("").build(),
                Offer.builder().url("").title("").price(9.0, CURRENCY).searcher("").build(),
                Offer.builder().url("").title("").price(10.0, CURRENCY).searcher("").build(),
                Offer.builder().url("").title("").price(0.0, CURRENCY).searcher("").build(),
                Offer.builder().url("").title("").price(20.0, CURRENCY).searcher("").build()
        };
    }

    @Test
    public void offersOutsideOfPriceRangeAreFiltered() {
        Observable<Offer> filteredObservable = filter.process(query, Observable.fromArray(offers));
        TestObserver<Offer> subscriber = publishSubject.delay(100, TimeUnit.MILLISECONDS, scheduler).test();
        filteredObservable.subscribe(subscriber);

        subscriber.assertNoErrors();
        subscriber.assertValueCount(4);
        subscriber.assertValues(offers[0], offers[2], offers[4], offers[6]);
    }
}
