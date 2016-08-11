package de.hanbei.rxsearch.server;

import com.google.common.collect.Lists;
import de.hanbei.rxsearch.coordination.ResponseHandler;
import de.hanbei.rxsearch.coordination.SearchCoordinator;
import de.hanbei.rxsearch.filter.FilterCoordinator;
import de.hanbei.rxsearch.filter.PriceFilter;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.Searcher;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import rx.Observable;

import java.util.List;
import java.util.Optional;

class SearchRouter implements Handler<RoutingContext> {


    private final SearchCoordinator searchCoordinator;
    private final FilterCoordinator filterCoordinator;


    public SearchRouter(List<Searcher> searcher) {
        this.searchCoordinator = new SearchCoordinator(searcher);
        this.filterCoordinator = new FilterCoordinator(Lists.newArrayList(new PriceFilter()));
    }

    @Override
    public void handle(RoutingContext routingContext) {
        String keyword = routingContext.request().getParam("q");
        String country = Optional.ofNullable(routingContext.request().getParam("country")).orElse("de");
        String requestId = Optional.ofNullable(routingContext.request().getHeader("X-Request-ID")).orElse("some_id");

        Query query = Query.builder().keywords(keyword).requestId(requestId).country(country).build();

        ResponseHandler responseHandler = new VertxResponseHandler(routingContext);

        Observable<Offer> offerObservable = searchCoordinator.startSearch(query, responseHandler);

        filterCoordinator.filter(query, offerObservable).toList().subscribe(
                responseHandler::handleSuccess,
                responseHandler::handleError
        );

    }


}
