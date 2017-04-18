package de.hanbei.rxsearch.events

import de.hanbei.rxsearch.model.Offer
import de.hanbei.rxsearch.server.SearchRequestConfiguration

class LoggedSearchContainer(val requestId: String, val searchConfiguraton: SearchRequestConfiguration) {

    var result: Result = Result.OK
    var numOffers: Int = 0
    var error: Throwable? = null
    var searcherMap = mutableMapOf<String, List<Offer>>()
    var processorMap = mutableMapOf<String, List<Offer>>()

    fun addOffer(searcher: String, offer: Offer): Unit {
        val offerList = searcherMap.getOrDefault(searcher, mutableListOf())
        searcherMap.put(searcher, offerList + offer)
    }

    fun addProcessor(processor: String, isFilter: Boolean, offers: List<Offer>): Unit {
        processorMap.put(processor, offers);
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

