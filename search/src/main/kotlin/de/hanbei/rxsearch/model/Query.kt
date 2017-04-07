package de.hanbei.rxsearch.model

data class Query internal constructor(private val keywords: String, private val requestId: String, private val country: String, private val price: Money? = null) {

    fun keywords(): String {
        return keywords
    }

    fun requestId(): String {
        return requestId
    }

    fun country(): String {
        return country
    }

    fun price(): Money? {
        return price
    }

    companion object {
        @JvmStatic
        fun builder(): QueryBuilder.KeywordStep {
            return QueryBuilder.Steps()
        }
    }
}