package de.hanbei.rxsearch.model

import com.google.common.base.Preconditions
import java.util.Currency

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
        fun builder(): KeywordStep {
            return Steps()
        }

        interface KeywordStep {
            fun keywords(keywords: String): RequestIdStep
        }

        interface RequestIdStep {
            fun requestId(requestId: String): CountryStep
        }

        interface CountryStep {
            fun country(country: String): OtherStep
        }

        interface OtherStep : BuildStep {
            fun price(price: Money): OtherStep

            fun price(amount: Double?, currency: String): OtherStep

            fun price(amount: Double?, currency: Currency): OtherStep
        }


        interface BuildStep {
            fun build(): Query
        }

        internal class Steps : KeywordStep, RequestIdStep, CountryStep, BuildStep, OtherStep {

            private var keywords: String = ""
            private var requestId: String = ""
            private var country: String = ""
            private var price: Money? = null

            override fun keywords(keywords: String): RequestIdStep {
                Preconditions.checkArgument(keywords.isNotBlank(), "Keywords is not allowed to be empty")
                this.keywords = keywords
                return this
            }

            override fun requestId(requestId: String): CountryStep {
                Preconditions.checkArgument(requestId.isNotBlank(), "requestId is not allowed to be empty")
                this.requestId = requestId
                return this
            }

            override fun country(country: String): OtherStep {
                Preconditions.checkArgument(country.isNotBlank(), "Country is not allowed to be empty")
                this.country = country
                return this
            }

            override fun build(): Query {
                return Query(keywords, requestId, country, price)
            }

            override fun price(price: Money): OtherStep {
                this.price = price
                return this
            }

            override fun price(amount: Double?, currency: String): OtherStep {
                return price(Money(amount, currency))
            }

            override fun price(amount: Double?, currency: Currency): OtherStep {
                return price(Money(amount, currency))
            }
        }
    }
}