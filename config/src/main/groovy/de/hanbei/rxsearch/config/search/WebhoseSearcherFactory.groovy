package de.hanbei.rxsearch.config.search

import de.hanbei.rxsearch.searcher.webhose.WebhoseSearcher
import okhttp3.OkHttpClient

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