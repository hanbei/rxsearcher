package de.hanbei.rxsearch.model

import com.google.common.base.Preconditions
import java.util.Currency

data class Query internal constructor(val keywords: String, val requestId: String, val country: String, val price: Money?, val manufacturer: String?, val ean: String?, val upc: String?, val asin: String?) {

    /*
    @JsonProperty("browser")
    private val browser: String? = null
    @JsonProperty("merchant_core_url")
    private val merchantCoreUrl: String? = null
    @JsonProperty("product_url")
    private val productUrl: String? = null
    @JsonProperty("schema_title")
    private val schemaTitle: String? = null
    @JsonProperty("og_title")
    private val ogTitle: String? = null
    @NotNull(message = "currency must not be null")
    @JsonProperty("product_number")
    private val productNumber: String? = null
    @JsonProperty("model_number")
    private val modelNumber: String? = null
    @JsonProperty("category")
    private val category: String? = null
    @JsonProperty("referrer")
    private val referrer: String? = null
    */

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

            fun manufacturer(manufacturer: String): OtherStep

            fun ean(ean: String): OtherStep

            fun upc(upc: String): OtherStep

            fun asin(asin: String): OtherStep

        }


        interface BuildStep {
            fun build(): Query
        }

        internal class Steps : KeywordStep, RequestIdStep, CountryStep, BuildStep, OtherStep {

            private var keywords: String = ""
            private var requestId: String = ""
            private var country: String = ""
            private var price: Money? = null
            private var manufacturer: String? = null
            private var ean: String? = null
            private var upc: String? = null
            private var asin: String? = null

            override fun keywords(keywords: String): RequestIdStep {
                Preconditions.checkArgument(keywords.isNotBlank(), "Keywords is not allowed to be empty")
                this.keywords = keywords
                return this
            }

            override fun requestId(requestId: String): CountryStep {
                Preconditions.checkArgument(requestId.isNotBlank(), "getRequestId is not allowed to be empty")
                this.requestId = requestId
                return this
            }

            override fun country(country: String): OtherStep {
                Preconditions.checkArgument(country.isNotBlank(), "Country is not allowed to be empty")
                this.country = country
                return this
            }


            override fun build(): Query {
                return Query(keywords, requestId, country, price, manufacturer, ean, upc, asin)
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

            override fun manufacturer(manufacturer: String): OtherStep {
                this.manufacturer = manufacturer
                return this
            }

            override fun ean(ean: String): OtherStep {
                this.ean = ean
                return this
            }

            override fun upc(upc: String): OtherStep {
                this.upc = upc
                return this
            }

            override fun asin(asin: String): OtherStep {
                this.asin = asin
                return this
            }
        }
    }
}