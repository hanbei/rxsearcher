package de.hanbei.rxsearch.events

import de.hanbei.rxsearch.model.Offer
import de.hanbei.rxsearch.model.Query
import de.hanbei.rxsearch.searcher.SearcherException

class Topics {
    companion object {
        @JvmStatic
        fun searcherError(): String = "de.hanbei.searcher.error"

        @JvmStatic
        fun searcherCompleted(): String = "de.hanbei.searcher.completed"

        @JvmStatic
        fun searcherResult(): String = "de.hanbei.searcher.result"

        @JvmStatic
        fun searchFinished(): String = "de.hanbei.search.finished"

        @JvmStatic
        fun searchFailed(): String = "de.hanbei.search.failed"
    }
}

data class SearcherCompletedEvent(val requestId: String, val searcher: String, val query: Query)

data class SearcherErrorEvent(val requestId: String, val searcher: String, val exception: SearcherException)

data class SearcherResultEvent(val requestId: String, val searcher: String, val offer: Offer)

data class SearchFinishedEvent(val requestId: String, val numberOfOffers: Int)

data class SearchFailedEvent(val requestId: String, val error: Throwable)

