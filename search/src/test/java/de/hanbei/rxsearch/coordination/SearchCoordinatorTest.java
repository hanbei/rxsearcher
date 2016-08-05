package de.hanbei.rxsearch.coordination;

import com.google.common.collect.Lists;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.Searcher;
import de.hanbei.rxsearch.searcher.SearcherException;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SearchCoordinatorTest {

    private static final String MESSAGE = "message";
    private static final String QUERY = "query";
    private static final String ID = "id";
    private SearchCoordinator coordinator;
    private Searcher searcher;

    @Before
    public void setUp() {
        searcher = mock(Searcher.class);
        coordinator = new SearchCoordinator(Lists.newArrayList(searcher));
    }

    @Test
    public void searchReturnSucessfulCallsSuccessHandler() {
        when(searcher.search(any(Query.class))).thenReturn(Observable.just(Offer.builder().url("").title("").price(0.0, "EUR").searcher("test").build()));

        coordinator.startSearch(new Query(QUERY, ID), new ResponseHandler() {
            @Override
            public void handleSuccess(List<Offer> results) {
                assertThat(results.size(), is(not(0)));
            }

            @Override
            public void handleError(Throwable t) {
                fail(t.getMessage());
            }
        });
    }

    @Test
    public void searchThrowsCallsErrorHandler() {
        when(searcher.search(any(Query.class))).thenThrow(new SearcherException(new Query("q", ID), MESSAGE));

        coordinator.startSearch(new Query(QUERY, ID), new ResponseHandler() {
            @Override
            public void handleSuccess(List<Offer> results) {
                fail("Success not expected");
            }

            @Override
            public void handleError(Throwable t) {
                assertThat(t, instanceOf(SearcherException.class));
                assertThat(t.getMessage(), is(MESSAGE));
                assertThat(((SearcherException) t).getQuery(), is(new Query("q", ID)));
            }
        });
    }

    @Test
    public void searchThrowsRuntimeExceptionIsWrappedInSearcherException() {
        when(searcher.search(any(Query.class))).thenReturn(Observable.error(new IllegalArgumentException(MESSAGE)));

        coordinator.startSearch(new Query(QUERY, ID), new ResponseHandler() {
            @Override
            public void handleSuccess(List<Offer> results) {
                assertThat(results.size(), is(0));
            }

            @Override
            public void handleError(Throwable t) {
                fail("Global error not expected");
            }

            @Override
            public Observable<Offer> searcherError(SearcherException t) {
                assertThat(t, instanceOf(SearcherException.class));
                assertThat(t.getMessage(), containsString(MESSAGE));
                assertThat(t.getQuery(), is(new Query(QUERY, ID)));
                assertThat(t.getCause(), instanceOf(IllegalArgumentException.class));
                return Observable.empty();
            }
        });
    }

    @Test
    public void searchThrowsSearcherExceptionIsNotWrappedInSearcherException() {
        when(searcher.search(any(Query.class))).thenReturn(Observable.error(new SearcherException(new Query("q", "i"), MESSAGE)));

        coordinator.startSearch(new Query(QUERY, ID), new ResponseHandler() {
            @Override
            public void handleSuccess(List<Offer> results) {
                // do nothing we are only interested in searcherError
            }

            @Override
            public void handleError(Throwable t) {
                fail("Global error not expected");
            }

            @Override
            public Observable<Offer> searcherError(SearcherException t) {
                assertThat(t, instanceOf(SearcherException.class));
                assertThat(t.getMessage(), containsString(MESSAGE));
                assertThat(t.getQuery(), is(new Query("q", "i")));
                assertThat(t.getCause(), is(nullValue()));
                return Observable.empty();
            }
        });
    }

}