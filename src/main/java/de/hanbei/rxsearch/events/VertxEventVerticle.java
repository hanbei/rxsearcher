package de.hanbei.rxsearch.events;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxEventVerticle extends AbstractVerticle {


    private static final Logger LOGGER = LoggerFactory.getLogger(VertxEventVerticle.class);

    private MessageConsumer<SearcherCompletedEvent> finishedConsumer;
    private MessageConsumer<SearcherErrorEvent> errorConsumer;
    private MessageConsumer<SearcherResultEvent> resultConsumer;

    @Override
    public void start(Future<Void> startFuture) {
        vertx.eventBus().registerDefaultCodec(SearcherCompletedEvent.class, new SearcherFinishedEventCodec());
        vertx.eventBus().registerDefaultCodec(SearcherErrorEvent.class, new SearcherErrorEventCodec());
        vertx.eventBus().registerDefaultCodec(SearcherResultEvent.class, new SearcherResultEventCodec());

        finishedConsumer = vertx.eventBus().consumer(Topics.searcherCompleted(), this::searcherCompleted);
        errorConsumer = vertx.eventBus().consumer(Topics.searcherError(), this::searcherError);
        resultConsumer = vertx.eventBus().consumer(Topics.searcherResult(), this::searcherResult);

        LOGGER.info("Started VertxEventVerticle");
        startFuture.complete();
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        finishedConsumer.unregister();
        errorConsumer.unregister();
        resultConsumer.unregister();
        stopFuture.complete();
    }

    private void searcherCompleted(Message<SearcherCompletedEvent> message) {
        SearcherCompletedEvent event = message.body();
        LOGGER.info("searcher {} completed for {}", event.getSearcher(), event.getQuery());
    }

    private void searcherError(Message<SearcherErrorEvent> message) {
        SearcherErrorEvent event = message.body();
        LOGGER.warn("Error in searcher " + event.getSearcher(), event.getException());
    }

    private void searcherResult(Message<SearcherResultEvent> message) {
        SearcherResultEvent event = message.body();
        LOGGER.info("searcher {} got result for {}", event.getSearcher(), event.getOffer());
    }

    private static class SearcherFinishedEventCodec implements MessageCodec<SearcherCompletedEvent, SearcherCompletedEvent> {

        @Override
        public void encodeToWire(Buffer buffer, SearcherCompletedEvent o) {
            JsonObject entries = JsonObject.mapFrom(o);
            entries.writeToBuffer(buffer);
        }

        @Override
        public SearcherCompletedEvent decodeFromWire(int pos, Buffer buffer) {
            return buffer.toJsonObject().mapTo(SearcherCompletedEvent.class);
        }

        @Override
        public SearcherCompletedEvent transform(SearcherCompletedEvent o) {
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
}