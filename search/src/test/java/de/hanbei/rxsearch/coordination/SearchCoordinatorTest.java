package de.hanbei.rxsearch.coordination;

import com.google.common.collect.Lists;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.Searcher;
import de.hanbei.rxsearch.searcher.SearcherException;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SearchCoordinatorTest {

    private static final String MESSAGE = "message";
    private static final Query QUERY = Query.builder().keywords("query").requestId("id").country("de").build();
    private static final Query OTHER_QUERY = Query.builder().keywords("q").requestId("i").country("de").build();
    private SearchCoordinator coordinator;
    private Searcher searcher;

    @Before
    public void setUp() {
        searcher = mock(Searcher.class);
        coordinator = new SearchCoordinator(Lists.newArrayList(searcher));
    }

    @Test
    public void searchReturnSucessfulCallsSuccessHandler() {
        Offer offer = Offer.builder().url("").title("").price(0.0, "EUR").searcher("test").build();
        when(searcher.search(any(Query.class))).thenReturn(Observable.just(offer));

        TestSubscriber<Offer> subscriber = new TestSubscriber<>();
        coordinator.startSearch(QUERY, new SearcherErrorHandler() {
            @Override
            public Observable<Offer> searcherError(SearcherException t) {
                fail("Should not throw error");
                return Observable.empty();
            }
        }).subscribe(subscriber);
        subscriber.assertCompleted();
        subscriber.assertValue(offer);
    }

    @Test
    public void searchThrowsCallsErrorHandler() {
        when(searcher.search(any(Query.class))).thenThrow(new SearcherException(OTHER_QUERY, MESSAGE));

        TestSubscriber<Offer> subscriber = new TestSubscriber<>();
        coordinator.startSearch(QUERY, new SearcherErrorHandler() {
            @Override
            public Observable<Offer> searcherError(SearcherException t) {
                fail("Should not throw error");
                return Observable.empty();
            }
        }).subscribe(subscriber);
        subscriber.assertNotCompleted();
        subscriber.assertNoValues();
    }

    @Test
    public void searchThrowsRuntimeExceptionIsWrappedInSearcherException() {
        when(searcher.search(any(Query.class))).thenReturn(Observable.error(new IllegalArgumentException(MESSAGE)));

        TestSubscriber<Offer> subscriber = new TestSubscriber<>();
        coordinator.startSearch(QUERY, new SearcherErrorHandler() {
            @Override
            public Observable<Offer> searcherError(SearcherException t) {
                assertThat(t, instanceOf(SearcherException.class));
                assertThat(t.getMessage(), containsString(MESSAGE));
                assertThat(t.getQuery(), is(QUERY));
                assertThat(t.getCause(), instanceOf(IllegalArgumentException.class));
                return Observable.empty();
            }
        }).subscribe(subscriber);
        subscriber.assertCompleted();
        subscriber.assertNoValues();
    }

    @Test
    public void searchThrowsSearcherExceptionIsNotWrappedInSearcherException() {
        when(searcher.search(any(Query.class))).thenReturn(Observable.error(new SearcherException(OTHER_QUERY, MESSAGE)));

        coordinator.startSearch(QUERY, new SearcherErrorHandler() {
            @Override
            public Observable<Offer> searcherError(SearcherException t) {
                assertThat(t, instanceOf(SearcherException.class));
                assertThat(t.getMessage(), containsString(MESSAGE));
                assertThat(t.getQuery(), is(OTHER_QUERY));
                assertThat(t.getCause(), is(nullValue()));
                return Observable.empty();
            }
        });
    }

}