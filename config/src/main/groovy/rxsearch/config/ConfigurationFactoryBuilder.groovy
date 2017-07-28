package rxsearch.config

import de.hanbei.rxsearch.config.search.*
import okhttp3.OkHttpClient
import rxsearch.config.filter.NoImageFilterFactory
import rxsearch.config.search.DuckDuckGoSearcherFactory
import rxsearch.config.search.GithubSearcherFactory
import rxsearch.config.search.WebhoseSearcherFactory

class ConfigurationFactoryBuilder extends FactoryBuilderSupport {

    private static final String SEARCHER = "searcher"
    private static final String FILTER = "filter"
    private static final String CONFIG = "config"


    private final OkHttpClient httpClient
    private ConfigurationFactory config

    ConfigurationFactoryBuilder(OkHttpClient httpClient) {
        this.httpClient = httpClient
        config = new ConfigurationFactory()
        setMethodMissingDelegate {
            throw new ConfigurationException("encountered unknown config statement")
        }
        registerFactories();
    }

    def registerFactories() {
        registerFactory(CONFIG, config)
        registerFactory(SEARCHER, new SearcherConfigurationFactory())
        registerFactory(FILTER, new FilterConfigurationFactory())

        registerSearcher()
        registerFilter()
    }

    private void registerSearcher() {
        registerFactory("duckduckgo", new DuckDuckGoSearcherFactory(httpClient))
        registerFactory("webhose", new WebhoseSearcherFactory(httpClient))
        registerFactory("github", new GithubSearcherFactory(httpClient))
    }

    private void registerFilter() {
        registerFactory("image", new NoImageFilterFactory())
    }


}