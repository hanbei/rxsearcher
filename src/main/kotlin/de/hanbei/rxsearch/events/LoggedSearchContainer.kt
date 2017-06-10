package de.hanbei.rxsearch.events

import de.hanbei.rxsearch.model.Hit
import de.hanbei.rxsearch.server.SearchRequestConfiguration

class LoggedSearchContainer(val requestId: String, val searchConfiguraton: SearchRequestConfiguration) {

    var result: Result = Result.OK
    var numOffers: Int = 0
    var error: Throwable? = null
    var searcherMap = mutableMapOf<String, List<Hit>>()
    var processorMap = mutableMapOf<String, List<Hit>>()

    fun addOffer(searcher: String, hit: Hit): Unit {
        val offerList = searcherMap.getOrDefault(searcher, mutableListOf())
        searcherMap.put(searcher, offerList + hit)
    }

    fun addProcessor(processor: String, isFilter: Boolean, hits: List<Hit>): Unit {
        processorMap.put(processor, hits);
    }

    fun failed(error: Throwable): Unit {
        this.result = Result.FAILED
        this.error = error
    }

    fun success(numberOfOffers: Int): Unit {
        this.result = Result.OK
        this.numOffers = numberOfOffers
    }

    enum class Result {
        FAILED, OK
    }

}

