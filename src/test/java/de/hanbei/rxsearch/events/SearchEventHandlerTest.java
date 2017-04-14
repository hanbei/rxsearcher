package de.hanbei.rxsearch.events;

import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.model.User;
import de.hanbei.rxsearch.searcher.SearcherException;
import io.vertx.core.eventbus.EventBus;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SearchEventHandlerTest {

    private SearchEventHandler searchEventHandler;
    private EventBus eventBus;

    @Before
    public void setup() {
        eventBus = mock(EventBus.class);
        searchEventHandler = new SearchEventHandler(eventBus);
    }

    @Test
    public void searcherCompleted() throws Exception {
        Query query = Query.builder().keywords("term").requestId("requestId").country("de").user(new User("user_id", "partner_id", "partner_sub_id")).build();
        searchEventHandler.searcherCompleted("requestId", "searcher", query);

        verify(eventBus).publish(Topics.searcherCompleted(), new SearcherCompletedEvent("requestId", "searcher", query));
    }

    @Test
    public void searcherError() throws Exception {
        SearcherException exception = new SearcherException("message");
        searchEventHandler.searcherError("requestId", "searcher", exception);

        verify(eventBus).publish(Topics.searcherError(), new SearcherErrorEvent("requestId", "searcher", exception));
    }

    @Test
    public void searcherResult() throws Exception {
        Offer offer = Offer.builder().url("url").title("title").price(0.0, "USD").searcher("searcher1").build();
        searchEventHandler.searcherResult("requestId", "searcher", offer);

        verify(eventBus).publish(Topics.searcherResult(), new SearcherResultEvent("requestId", "searcher", offer));
    }

}