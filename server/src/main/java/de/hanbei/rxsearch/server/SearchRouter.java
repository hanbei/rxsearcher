package de.hanbei.rxsearch.server;

import de.hanbei.rxsearch.coordination.ResponseHandler;
import de.hanbei.rxsearch.coordination.SearchCoordinator;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.Searcher;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Optional;

public class SearchRouter implements Handler<RoutingContext> {


    private final SearchCoordinator searchCoordinator;

    public SearchRouter(List<Searcher> searcher) {
        this.searchCoordinator = new SearchCoordinator(searcher);
    }

    @Override
    public void handle(RoutingContext routingContext) {
        String keyword = routingContext.request().getParam("q");
        String requestId = Optional.ofNullable(routingContext.request().getHeader("X-Request-ID")).orElse("some_id");

        Query query = new Query(keyword, requestId);
        ResponseHandler responseHandler = new VertxResponseHandler(routingContext);
        searchCoordinator.startSearch(query, responseHandler);
    }


}
