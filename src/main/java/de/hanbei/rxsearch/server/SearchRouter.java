package de.hanbei.rxsearch.server;

import de.hanbei.rxsearch.coordination.SearchCoordinator;
import de.hanbei.rxsearch.events.OfferProcessedEvent;
import de.hanbei.rxsearch.events.SearchStartedEvent;
import de.hanbei.rxsearch.events.SearcherCompletedEvent;
import de.hanbei.rxsearch.events.SearcherErrorEvent;
import de.hanbei.rxsearch.events.SearcherResultEvent;
import de.hanbei.rxsearch.filter.OfferProcessor;
import de.hanbei.rxsearch.filter.OfferProcessorCoordinator;
import de.hanbei.rxsearch.model.Offer;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.model.User;
import de.hanbei.rxsearch.searcher.Searcher;
import io.reactivex.Observable;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class SearchRouter implements Handler<RoutingContext> {

    private final SearchCoordinator searchCoordinator;
    private final OfferProcessorCoordinator filterCoordinator;
    private final EventBus eventBus;
    private ResponseHandler responseHandler;

    public SearchRouter(List<Searcher> searcher, List<OfferProcessor> processors, EventBus eventBus) {
        this.eventBus = eventBus;

        this.searchCoordinator = new SearchCoordinator(searcher,
                (requestId, s, t) -> {
                    eventBus.publish(SearcherErrorEvent.topic(), new SearcherErrorEvent(requestId, s, t));
                },
                (requestId, s, query) -> {
                    eventBus.publish(SearcherCompletedEvent.topic(), new SearcherCompletedEvent(requestId, s, query));
                },
                (requestId, s, offer) -> {
                    eventBus.publish(SearcherResultEvent.topic(), new SearcherResultEvent(requestId, s, offer));
                });
        this.filterCoordinator = new OfferProcessorCoordinator(processors,
                (requestId, processorName, filter, remainingOffers) -> eventBus.publish(OfferProcessedEvent.topic(), new OfferProcessedEvent(requestId, processorName, filter, remainingOffers))
        );
        responseHandler = new ResponseHandler();
    }

    @Override
    public void handle(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        String requestId = Optional.ofNullable(request.getHeader("X-Request-ID")).orElse(UUID.randomUUID().toString());
        boolean logSearch = Optional.ofNullable(Boolean.valueOf(request.getParam("logSearch"))).orElse(false);
        Query q = extractQuery(routingContext, requestId);

        eventBus.publish(SearchStartedEvent.topic(), new SearchStartedEvent(requestId, new SearchRequestConfiguration(requestId, logSearch)));

        Observable<Offer> offerObservable = searchCoordinator.startSearch(q);

        filterCoordinator.filter(q, offerObservable).toList().subscribe(
                o -> responseHandler.handleSuccess(routingContext, requestId, o),
                t -> responseHandler.handleError(routingContext, requestId, t)
        );
    }

    private Query extractQuery(RoutingContext routingContext, String requestId) {
        JsonObject queryAsJson = routingContext.getBodyAsJson();

        JsonObject query = queryAsJson.getJsonObject("query");

        User user = extractUser(query);

        Query.Companion.OtherStep builder = Query.builder().keywords(query.getString("keywords"))
                .requestId(requestId).country(query.getString("country")).user(user);

        return builder.build();
    }

    private User extractUser(JsonObject query) {
        JsonObject userJson = query.getJsonObject("user");
        return new User(userJson.getString("id"), userJson.getString("partnerId"), userJson.getString("partnerSubId"));
    }

}

