package de.hanbei.rxsearch.filter.impl;

import de.hanbei.rxsearch.model.Hit;
import de.hanbei.rxsearch.model.Query;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;

public class NoImageFilterTest {

    private static final String CURRENCY = "EUR";
    private NoImageFilter filter;
    private Query query;
    private Hit[] offers;

    @Before
    public void setup() {
        filter = new NoImageFilter();
        query = Query.builder().keywords("keywords").requestId("requestId").country("de").build();
        offers = new Hit[]{
                Hit.builder().url("").title("").searcher("").image("not null").build(),
                Hit.builder().url("").title("").searcher("").build(),
                Hit.builder().url("").title("").searcher("").image("not null").build(),
                Hit.builder().url("").title("").searcher("").build(),
                Hit.builder().url("").title("").searcher("").image("not null").build(),
                Hit.builder().url("").title("").searcher("").build(),
                Hit.builder().url("").title("").searcher("").image("not null").build()
        };
    }

    @Test
    public void offersOutsideOfPriceRangeAreFiltered() {
        Observable<Hit> filteredObservable = filter.process(query, Observable.fromArray(offers));
        TestObserver<Hit> subscriber = new TestObserver<>();
        filteredObservable.subscribe(subscriber);

        subscriber.assertNoErrors();
        subscriber.assertValueCount(4);
        subscriber.assertValues(offers[0], offers[2], offers[4], offers[6]);
    }
}
