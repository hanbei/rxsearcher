package de.hanbei.rxsearch.events;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxEventVerticle extends AbstractVerticle {


    private static final Logger LOGGER = LoggerFactory.getLogger(VertxEventVerticle.class);
    private MessageConsumer<Object> finishedConsumer;
    private MessageConsumer<Object> errorConsumer;
    private MessageConsumer<Object> resultConsumer;

    @Override
    public void start(Future<Void> startFuture) {
        vertx.eventBus().registerDefaultCodec(SearcherFinishedEvent.class, new SearcherFinishedEventCodec());
        vertx.eventBus().registerDefaultCodec(SearcherErrorEvent.class, new SearcherErrorEventCodec());
        vertx.eventBus().registerDefaultCodec(SearcherResultEvent.class, new SearcherResultEventCodec());

        finishedConsumer = vertx.eventBus().consumer("de.hanbei.searcher.finished", message -> searcherCompleted((SearcherFinishedEvent) message.body()));
        errorConsumer = vertx.eventBus().consumer("de.hanbei.searcher.error", message -> searcherError((SearcherErrorEvent) message.body()));
        resultConsumer = vertx.eventBus().consumer("de.hanbei.searcher.result", message -> searcherResult((SearcherResultEvent) message.body()));
        LOGGER.info("Started VertxEventVerticle");
        startFuture.complete();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        finishedConsumer.unregister();
        errorConsumer.unregister();
        resultConsumer.unregister();
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);
    }

    private void searcherCompleted(SearcherFinishedEvent event) {
        LOGGER.info("searcher {} completed for {}", event.getSearcher(), event.getQuery());
    }

    private void searcherError(SearcherErrorEvent event) {
        LOGGER.warn("Error in searcher " + event.getSearcher(), event.getException());
    }

    private void searcherResult(SearcherResultEvent event) {
        //LOGGER.info("searcher {} got result for {}", event.getSearcher(), event.getOffer());
    }

    private static class SearcherFinishedEventCodec implements MessageCodec<SearcherFinishedEvent, SearcherFinishedEvent> {

        @Override
        public void encodeToWire(Buffer buffer, SearcherFinishedEvent o) {
            JsonObject entries = JsonObject.mapFrom(o);
            entries.writeToBuffer(buffer);
        }

        @Override
        public SearcherFinishedEvent decodeFromWire(int pos, Buffer buffer) {
            return buffer.toJsonObject().mapTo(SearcherFinishedEvent.class);
        }

        @Override
        public SearcherFinishedEvent transform(SearcherFinishedEvent o) {
            return o;
        }

        @Override
        public String name() {
            return SearcherFinishedEventCodec.class.getSimpleName();
        }

        @Override
        public byte systemCodecID() {
            return -1;
        }
    }

    private static class SearcherResultEventCodec implements MessageCodec<SearcherResultEvent, SearcherResultEvent> {

        @Override
        public void encodeToWire(Buffer buffer, SearcherResultEvent o) {
            JsonObject entries = JsonObject.mapFrom(o);
            entries.writeToBuffer(buffer);
        }

        @Override
        public SearcherResultEvent decodeFromWire(int pos, Buffer buffer) {
            return buffer.toJsonObject().mapTo(SearcherResultEvent.class);
        }

        @Override
        public SearcherResultEvent transform(SearcherResultEvent o) {
            return o;
        }

        @Override
        public String name() {
            return SearcherResultEventCodec.class.getSimpleName();
        }

        @Override
        public byte systemCodecID() {
            return -1;
        }
    }

    private static class SearcherErrorEventCodec implements MessageCodec<SearcherErrorEvent, SearcherErrorEvent> {

        @Override
        public void encodeToWire(Buffer buffer, SearcherErrorEvent o) {
            JsonObject entries = JsonObject.mapFrom(o);
            entries.writeToBuffer(buffer);
        }

        @Override
        public SearcherErrorEvent decodeFromWire(int pos, Buffer buffer) {
            return buffer.toJsonObject().mapTo(SearcherErrorEvent.class);
        }

        @Override
        public SearcherErrorEvent transform(SearcherErrorEvent o) {
            return o;
        }

        @Override
        public String name() {
            return SearcherErrorEventCodec.class.getSimpleName();
        }

        @Override
        public byte systemCodecID() {
            return -1;
        }
    }
}