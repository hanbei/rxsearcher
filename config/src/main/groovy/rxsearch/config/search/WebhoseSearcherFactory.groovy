package rxsearch.config.search

import okhttp3.OkHttpClient
import rxsearch.searcher.webhose.WebhoseSearcher

class WebhoseSearcherFactory extends AbstractSearcherFactory {

    WebhoseSearcherFactory(OkHttpClient httpClient) {
        super(httpClient)
    }

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        checkAttributes("WebhoseSearcher", attributes, NAME, "key")
        return new WebhoseSearcher(attributes.get(NAME) as String, attributes.get("key") as String, httpClient)
    }


}