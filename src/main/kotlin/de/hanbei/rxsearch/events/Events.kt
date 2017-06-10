package de.hanbei.rxsearch.events

import de.hanbei.rxsearch.model.Hit
import de.hanbei.rxsearch.model.Query
import de.hanbei.rxsearch.searcher.SearcherException
import de.hanbei.rxsearch.server.SearchRequestConfiguration
import io.vertx.core.eventbus.MessageCodec

data class SearcherStartedEvent(val requestId: String, val searcher: String) {
    companion object {
        @JvmStatic
        fun Codec(): MessageCodec<SearcherStartedEvent, SearcherStartedEvent> = object : PojoToJsonCodec<SearcherStartedEvent>(SearcherStartedEvent::class.java) {}

        @JvmStatic
        fun topic(): String = "de.hanbei.searcher.started"
    }
}

data class SearcherResultEvent(val requestId: String, val searcher: String, val hit: Hit) {
    companion object {
        @JvmStatic
        fun Codec(): MessageCodec<SearcherResultEvent, SearcherResultEvent> = object : PojoToJsonCodec<SearcherResultEvent>(SearcherResultEvent::class.java) {}

        @JvmStatic
        fun topic(): String = "de.hanbei.searcher.result"
    }
}

data class SearcherCompletedEvent(val requestId: String, val searcher: String, val query: Query) {
    companion object {
        @JvmStatic
        fun Codec(): MessageCodec<SearcherCompletedEvent, SearcherCompletedEvent> = object : PojoToJsonCodec<SearcherCompletedEvent>(SearcherCompletedEvent::class.java) {}

        @JvmStatic
        fun topic(): String = "de.hanbei.searcher.completed"
    }
}

data class SearcherErrorEvent(val requestId: String, val searcher: String, val exception: SearcherException) {
    companion object {
        @JvmStatic
        fun Codec(): MessageCodec<SearcherErrorEvent, SearcherErrorEvent> = object : PojoToJsonCodec<SearcherErrorEvent>(SearcherErrorEvent::class.java) {}

        @JvmStatic
        fun topic(): String = "de.hanbei.searcher.error"
    }
}

data class SearchStartedEvent(val requestId: String, val searchConfiguraton: SearchRequestConfiguration) {
    companion object {
        @JvmStatic
        fun Codec(): MessageCodec<SearchStartedEvent, SearchStartedEvent> = object : PojoToJsonCodec<SearchStartedEvent>(SearchStartedEvent::class.java) {}

        @JvmStatic
        fun topic(): String = "de.hanbei.search.started"

    }
}


data class SearchFinishedEvent(val requestId: String, val numberOfOffers: Int) {
    companion object {
        @JvmStatic
        fun Codec(): MessageCodec<SearchFinishedEvent, SearchFinishedEvent> = object : PojoToJsonCodec<SearchFinishedEvent>(SearchFinishedEvent::class.java) {}

        @JvmStatic
        fun topic(): String = "de.hanbei.search.finished"
    }
}

data class SearchFailedEvent(val requestId: String, val error: Throwable) {
    companion object {
        @JvmStatic
        fun Codec(): MessageCodec<SearchFailedEvent, SearchFailedEvent> = object : PojoToJsonCodec<SearchFailedEvent>(SearchFailedEvent::class.java) {}

        @JvmStatic
        fun topic(): String = "de.hanbei.search.failed"
    }

}

data class OfferProcessedEvent(val requestId: String, val processor: String, val isFilter: Boolean, val hits: List<Hit>) {
    companion object {
        @JvmStatic
        fun Codec(): MessageCodec<OfferProcessedEvent, OfferProcessedEvent> = object : PojoToJsonCodec<OfferProcessedEvent>(OfferProcessedEvent::class.java) {}

        @JvmStatic
        fun topic(): String = "de.hanbei.search.offerProcessed"
    }

}


