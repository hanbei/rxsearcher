package rxsearch.model

import com.google.common.base.Preconditions

data class Query internal constructor(val keywords: String, val requestId: String, val country: String) {


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
            fun country(country: String): BuildStep
        }


        interface BuildStep {
            fun build(): Query
        }

        internal class Steps : KeywordStep, RequestIdStep, CountryStep, BuildStep {
            private var keywords: String = ""
            private var requestId: String = ""
            private var country: String = ""

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

            override fun country(country: String): BuildStep {
                Preconditions.checkArgument(country.isNotBlank(), "Country is not allowed to be empty")
                this.country = country
                return this
            }

            override fun build(): Query {
                return Query(keywords, requestId, country)
            }

        }
    }
}