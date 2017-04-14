package de.hanbei.rxsearch.events

import de.hanbei.rxsearch.model.Offer
import de.hanbei.rxsearch.model.Query
import de.hanbei.rxsearch.searcher.SearcherException
import de.hanbei.rxsearch.server.SearchRequestConfiguration
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.json.JsonObject

class Topics {
    companion object {
        @JvmStatic
        fun searcherStarted(): String = "de.hanbei.searcher.started"

        @JvmStatic
        fun searcherResult(): String = "de.hanbei.searcher.result"

        @JvmStatic
        fun searcherError(): String = "de.hanbei.searcher.error"

        @JvmStatic
        fun searcherCompleted(): String = "de.hanbei.searcher.completed"

        @JvmStatic
        fun searchStarted(): String = "de.hanbei.search.started"

        @JvmStatic
        fun searchFinished(): String = "de.hanbei.search.finished"

        @JvmStatic
        fun searchFailed(): String = "de.hanbei.search.failed"
    }
}

data class SearcherStartedEvent(val requestId: String, val searcher: String) {
    companion object {
        @JvmStatic
        fun Codec(): MessageCodec<SearcherStartedEvent, SearcherStartedEvent> = object : PojoToJsonCodec<SearcherStartedEvent>(SearcherStartedEvent::class.java) {}
    }
}

data class SearcherResultEvent(val requestId: String, val searcher: String, val offer: Offer) {
    companion object {
        @JvmStatic
        fun Codec(): MessageCodec<SearcherResultEvent, SearcherResultEvent> = object : PojoToJsonCodec<SearcherResultEvent>(SearcherResultEvent::class.java) {}
    }
}

data class SearcherCompletedEvent(val requestId: String, val searcher: String, val query: Query) {
    companion object {
        @JvmStatic
        fun Codec(): MessageCodec<SearcherCompletedEvent, SearcherCompletedEvent> = object : PojoToJsonCodec<SearcherCompletedEvent>(SearcherCompletedEvent::class.java) {}
    }
}

data class SearcherErrorEvent(val requestId: String, val searcher: String, val exception: SearcherException) {
    companion object {
        @JvmStatic
        fun Codec(): MessageCodec<SearcherErrorEvent, SearcherErrorEvent> = object : PojoToJsonCodec<SearcherErrorEvent>(SearcherErrorEvent::class.java) {}
    }
}

data class SearchStartedEvent(val requestId: String, val searchConfiguraton : SearchRequestConfiguration) {
    companion object {
        @JvmStatic
        fun Codec(): MessageCodec<SearchStartedEvent, SearchStartedEvent> = object : PojoToJsonCodec<SearchStartedEvent>(SearchStartedEvent::class.java) {}
    }
}


data class SearchFinishedEvent(val requestId: String, val numberOfOffers: Int) {
    companion object {
        @JvmStatic
        fun Codec(): MessageCodec<SearchFinishedEvent, SearchFinishedEvent> = object : PojoToJsonCodec<SearchFinishedEvent>(SearchFinishedEvent::class.java) {}
    }
}

data class SearchFailedEvent(val requestId: String, val error: Throwable) {
    companion object {
        @JvmStatic
        fun Codec(): MessageCodec<SearchFailedEvent, SearchFailedEvent> = object : PojoToJsonCodec<SearchFailedEvent>(SearchFailedEvent::class.java) {}
    }

}

open class PojoToJsonCodec<T>(val clasz: Class<T>) : MessageCodec<T, T> {

    override fun encodeToWire(buffer: Buffer, o: T) {
        val entries = JsonObject.mapFrom(o)
        entries.writeToBuffer(buffer)
    }

    override fun decodeFromWire(pos: Int, buffer: Buffer): T {
        return buffer.toJsonObject().mapTo(clasz)
    }

    override fun transform(o: T): T {
        return o
    }

    override fun name(): String {
        return clasz.simpleName
    }

    override fun systemCodecID(): Byte {
        return -1
    }
}