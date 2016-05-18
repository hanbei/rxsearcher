package de.hanbei.rxsearch.server;

import com.google.common.collect.Lists;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.searcher.Searcher;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import static org.mockito.Mockito.*;

public class SearchRouterTest {

    private SearchRouter router;
    private RoutingContext routingContext;

    private Searcher searcher1;
    private Searcher searcher2;

    private HttpServerRequest request;
    private HttpServerResponse response;

    @Before
    public void setUp() throws Exception {
        searcher1 = mock(Searcher.class);
        searcher2 = mock(Searcher.class);

        request = mock(HttpServerRequest.class);
        when(request.getParam("keyword")).thenReturn("search_term");
        response = mock(HttpServerResponse.class);
        when(response.putHeader(anyString(), anyString())).thenReturn(response);

        routingContext = mock(RoutingContext.class);
        when(routingContext.request()).thenReturn(request);
        when(routingContext.response()).thenReturn(response);

        router = new SearchRouter(Lists.newArrayList(searcher1, searcher2));
    }

    @Test
    public void whenSearcherRespondRendersCorrectJson() throws Exception {
        when(searcher1.search("search_term")).thenReturn(Observable.just(new Offer("title", "searcher1", "url")));
        when(searcher2.search("search_term")).thenReturn(Observable.just(new Offer("title", "searcher2", "url")));

        router.handle(routingContext);

        verify(response, times(1)).end("{\"results\":[{\"title\":\"title\",\"searchSource\":\"searcher1\",\"url\":\"url\",\"image\":\"\",\"price\":{\"currency\":\"USD\",\"amount\":0.0}},{\"title\":\"title\",\"searchSource\":\"searcher2\",\"url\":\"url\",\"image\":\"\",\"price\":{\"currency\":\"USD\",\"amount\":0.0}}]}");
    }

    @Test
    public void sendsErrorResponseWhenSearchThrows() {
        when(searcher1.search("search_term")).thenReturn(Observable.error(new RuntimeException("SearchError")));

        router.handle(routingContext);

        verify(routingContext, times(1)).fail(any(RuntimeException.class));
    }

}