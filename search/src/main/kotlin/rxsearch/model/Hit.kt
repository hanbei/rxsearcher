package rxsearch.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Hit private constructor(val url: String,
                                   val title: String,
                                   val searcher: String,
                                   val image: String?,
                                   val requestId: String?,
                                   val description: String?) {

    companion object {
        @JvmStatic
        fun builder(): UrlStep {
            return Steps()
        }

        @JvmStatic
        fun from(hit: Hit): OtherStep {
            val (url, title, searcher, image, requestId, description) = hit.copy()

            val offerBuilder = Steps()
                    .url(url).title(title).searcher(searcher)
                    .image(image).description(description)
                    .requestId(requestId)

            return offerBuilder
        }

        interface UrlStep {
            fun url(url: String): TitleStep
        }

        interface TitleStep {
            fun title(title: String): SearcherStep
        }

        interface SearcherStep {
            fun searcher(searcher: String): OtherStep
        }

        interface OtherStep : BuildStep {
            fun requestId(requestId: String?): OtherStep

            fun image(image: String?): OtherStep

            fun description(description: String?): OtherStep
        }

        interface BuildStep {
            fun build(): Hit
        }

        internal class Steps : UrlStep, TitleStep, OtherStep, SearcherStep, BuildStep {
            var url: String = ""
            var title: String = ""
            var searcher: String = ""
            var requestId: String? = null
            var image: String? = null
            var description: String? = null

            override fun requestId(requestId: String?): OtherStep {
                this.requestId = requestId
                return this
            }

            override fun url(url: String): TitleStep {
                this.url = url
                return this
            }

            override fun title(title: String): SearcherStep {
                this.title = title
                return this
            }

            override fun searcher(searcher: String): OtherStep {
                this.searcher = searcher
                return this
            }

            override fun image(image: String?): OtherStep {
                this.image = image
                return this
            }

            override fun description(description: String?): OtherStep {
                this.description = description
                return this
            }

            override fun build(): Hit {
                return Hit(this.url,
                        this.title,
                        this.searcher,
                        this.image,
                        this.requestId,
                        this.description)
            }
        }
    }
}