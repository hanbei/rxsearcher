package de.hanbei.rxsearch.server;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

/**
 * Created by hanbei on 3/25/16.
 */
public class VertxServer extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {
        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route("/search/:keyword").blockingHandler(routingContext -> {
            String keyword = routingContext.request().getParam("keyword").toLowerCase();
            routingContext.response().end(keyword);
        });

        httpServer.requestHandler(router::accept).listen(8080);
    }

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(VertxServer.class.getName());
    }
}
