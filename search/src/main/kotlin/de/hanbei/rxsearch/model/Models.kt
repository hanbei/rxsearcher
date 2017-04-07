package de.hanbei.rxsearch.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.google.common.collect.ImmutableList
import java.util.Currency

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Merchant internal constructor(val id: String, val name: String, val image: String, val category: String?) {

    companion object {
        @JvmStatic
        fun builder(): MerchantBuilder.NameStep {
            return MerchantBuilder.Steps()
        }
    }
}

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

data class Money(val amount: Double?, val currency: Currency) {

    constructor(amount: Double?, currency: String) : this(amount, Currency.getInstance(currency)) {}

    companion object {
        val NULL_PRICE: Money = Money(Double.NEGATIVE_INFINITY, Currency.getInstance("USD"))
    }

}


@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Offer internal constructor(private val steps: OfferBuilder.Steps) {

    val url: String
    val title: String
    val price: Money
    val searcher: String
    val merchant: Merchant?
    val image: String

    val category: String?
    val originalUrl: String?
    val shippingCosts: Money?
    val manufacturer: String?
    val description: String?
    val brand: String?

    val type: Type?
    val eec: EEC?
    val availability: Availability?
    val ecpc: Money?
    val listPrice: Money?

    //    private final Double productRating;
    //    private final Integer ratingReviewsCount;

    val eans: ImmutableList<String>
    val mpns: ImmutableList<String>
    val gtins: ImmutableList<String>
    val upcs: ImmutableList<String>

    init {
        this.url = steps.url
        this.title = steps.productName
        this.price = steps.price
        this.searcher = steps.searcher
        this.category = steps.category
        this.image = steps.image
        this.manufacturer = steps.manufacturer
        this.merchant = steps.merchant
        this.description = steps.description
        this.originalUrl = steps.originalUrl
        this.shippingCosts = steps.shippingCosts
        this.brand = steps.brand
        this.eec = steps.eec
        this.ecpc = steps.ecpc
        this.type = steps.type
        this.availability = steps.availability
        this.listPrice = steps.listPrice
        this.eans = ImmutableList.copyOf(steps.eans)
        this.mpns = ImmutableList.copyOf(steps.mpns)
        this.gtins = ImmutableList.copyOf(steps.gtins)
        this.upcs = ImmutableList.copyOf(steps.upcs)
    }

    enum class Type {
        PRODUCT, RELATED
    }

    enum class Availability {
        NOT_AVAILABLE, YELLOW, GREEN, UNKNOWN
    }

    enum class EEC {
        A_3PLUS, A_2PLUS, A_PLUS, A, B, C, D, E, F, G
    }

    companion object {
        @JvmStatic
        fun builder(): OfferBuilder.UrlStep {
            return OfferBuilder.Steps()
        }

        @JvmStatic
        fun from(offer: Offer): OfferBuilder.OtherStep {
            return OfferBuilder.Steps()
                    .url(offer.url).title(offer.title).price(offer.price).searcher(offer.searcher)
                    .category(offer.category).image(offer.image).description(offer.description)
                    .manufacturer(offer.manufacturer).originalUrl(offer.originalUrl).shippingCosts(offer.shippingCosts)
                    .merchant(offer.merchant).brand(offer.brand)
        }
    }
}

