package de.hanbei.rxsearch.events

import de.hanbei.rxsearch.model.Offer
import de.hanbei.rxsearch.model.Query
import de.hanbei.rxsearch.searcher.SearcherException

data class SearcherFinishedEvent(val searcher : String, val query : Query)

data class SearcherErrorEvent(val searcher : String, val exception : SearcherException)

data class SearcherResultEvent(val searcher : String, val offer : Offer)
