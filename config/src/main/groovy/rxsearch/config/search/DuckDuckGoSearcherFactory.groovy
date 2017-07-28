package rxsearch.config.search

import okhttp3.OkHttpClient
import rxsearch.searcher.duckduckgo.DuckDuckGoSearcher

class DuckDuckGoSearcherFactory extends AbstractSearcherFactory {

    DuckDuckGoSearcherFactory(OkHttpClient httpClient) {
        super(httpClient)
    }

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        checkAttributes("DuckDuckGoSearcher", attributes, NAME)
        return new DuckDuckGoSearcher(attributes.get(NAME) as String, httpClient)
    }

}