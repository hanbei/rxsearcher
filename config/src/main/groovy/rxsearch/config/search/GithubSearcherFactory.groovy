package rxsearch.config.search

import rxsearch.searcher.github.GithubSearcher
import okhttp3.OkHttpClient

class GithubSearcherFactory extends AbstractSearcherFactory {

    GithubSearcherFactory(OkHttpClient httpClient) {
        super(httpClient)
    }

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        checkAttributes("GithubSearcher", attributes, NAME, "repo")
        return new GithubSearcher(attributes.get(NAME) as String, attributes.get("repo") as String, httpClient)
    }


}