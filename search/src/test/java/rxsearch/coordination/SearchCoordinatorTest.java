package rxsearch.coordination;

import com.google.common.collect.Lists;
import rxsearch.model.Hit;
import rxsearch.model.Query;
import rxsearch.searcher.Searcher;
import rxsearch.searcher.SearcherException;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SearchCoordinatorTest {

    private static final String MESSAGE = "message";
    private static final Query QUERY = Query.builder().keywords("query").requestId("id").country("de").build();
    private static final Query OTHER_QUERY = Query.builder().keywords("q").requestId("i").country("de").build();
    private static final String SEARCHER_NAME = "test_searcher";

    private Searcher searcher;
    private Given given;

    @Before
    public void setUp() {
        given = new Given();
        searcher = mock(Searcher.class);
        when(searcher.getName()).thenReturn(SEARCHER_NAME);
    }

    @Test
    public void searchReturnSucessfulCallsSuccessHandler() {
        SearchCoordinator coordinator = given.givenCoordinatorNotExpectingError(searcher);

        Hit hit = Hit.builder().url("").title("").searcher("test").requestId("id").build();
        when(searcher.search(any(Query.class))).thenReturn(Observable.just(hit));

        TestObserver<Hit> subscriber = new TestObserver<>();
        coordinator.startSearch(QUERY).subscribe(subscriber);
        subscriber.assertComplete();
        subscriber.assertValue(hit);
    }

    @Test
    public void searchThrowsCallsErrorHandler() {
        SearchCoordinator coordinator = given.givenCoordinatorNotExpectingError(searcher);

        when(searcher.search(any(Query.class))).thenThrow(new SearcherException(MESSAGE).query(OTHER_QUERY));

        TestObserver<Hit> subscriber = new TestObserver<>();
        coordinator.startSearch(QUERY).subscribe(subscriber);
        subscriber.assertNotComplete();
        subscriber.assertNoValues();
    }

    @Test
    public void searchThrowsRuntimeExceptionIsWrappedInSearcherException() {
        SearchCoordinator coordinator = given.givenCoordinatorExpectingError(searcher, QUERY);

        when(searcher.search(any(Query.class))).thenReturn(Observable.error(new IllegalArgumentException(MESSAGE)));

        TestObserver<Hit> subscriber = new TestObserver<>();
        coordinator.startSearch(QUERY).subscribe(subscriber);

        subscriber.assertComplete();
        subscriber.assertNoValues();
    }

    @Test
    public void searchThrowsSearcherExceptionIsNotWrappedInSearcherException() {
        SearchCoordinator coordinator = given.givenCoordinatorExpectingError(searcher, OTHER_QUERY);

        when(searcher.search(any(Query.class))).thenReturn(Observable.error(new SearcherException(MESSAGE).query(OTHER_QUERY)));

        coordinator.startSearch(QUERY);
    }

    private static class Given {
        SearchCoordinator givenCoordinatorExpectingError(Searcher searcher, Query query) {
            return new SearchCoordinator(Lists.newArrayList(searcher),
                    (r, s, t) -> {
                        assertThat(r, is("id"));
                        assertThat(t, instanceOf(SearcherException.class));
                        assertThat(t.getMessage(), containsString(MESSAGE));
                        assertEquals(query, t.getQuery());
                        assertThat(t.getCause(), instanceOf(IllegalArgumentException.class));
                    },
                    (r, s, q) -> {
                        fail("Completion called but not expected");
                    }
            );
        }

        SearchCoordinator givenCoordinatorNotExpectingError(Searcher searcher) {
            return new SearchCoordinator(Lists.newArrayList(searcher),
                    (r, s, t) -> fail("Should not throw error"),
                    (r, s, q) -> {
                        assertThat(r, is("id"));
                        assertEquals(SEARCHER_NAME, s);
                        assertEquals(QUERY, q);
                    }
            );
        }
    }
}