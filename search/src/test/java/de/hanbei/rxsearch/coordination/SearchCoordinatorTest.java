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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SearchCoordinatorTest {

    private SearchCoordinator coordinator;
    private Searcher searcher;

    @Before
    public void setUp() throws Exception {
        searcher = mock(Searcher.class);
        coordinator = new SearchCoordinator(Lists.newArrayList(searcher));
    }

    @Test
    public void searchReturnSucessfulCallsSuccessHandler() throws Exception {
        when(searcher.search(any(Query.class))).thenReturn(Observable.just(Offer.builder().url("").title("").price(0.0, "EUR").searcher("test").build()));

        coordinator.startSearch(new Query("query", "id"), new ResponseHandler() {
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
    public void searchThrowsCallsErrorHandler() throws Exception {
        when(searcher.search(any(Query.class))).thenThrow(new SearcherException(new Query("q", "id"), "message"));

        coordinator.startSearch(new Query("query", "id"), new ResponseHandler() {
            @Override
            public void handleSuccess(List<Offer> results) {
                fail("Success not expected");
            }

            @Override
            public void handleError(Throwable t) {
                assertThat(t, instanceOf(SearcherException.class));
                assertThat(t.getMessage(), is("message"));
            }
        });
    }

}