package de.hanbei.rxsearch.config

import okhttp3.OkHttpClient
import de.hanbei.rxsearch.searcher.Searcher

class SearcherConfiguration {

    def configurationLoader
    def searchConfigBuilder

    SearcherConfiguration(OkHttpClient asynHttpClient) {
        this.configurationLoader = new ConfigurationLoader()
        this.searchConfigBuilder = new SearchConfigBuilder(asynHttpClient)
    }

    List<Searcher> loadConfiguration(String appName, String environment, String country) {
        String configuration = configurationLoader.load(appName, environment, country)

        try {
            def instance = new GroovyClassLoader().parseClass(configuration).newInstance()
            instance.binding = new BuilderBinding(builder: searchConfigBuilder)
            def searchers = instance.run()
            return searchers?.searchers
        } catch (ConfigurationException e) {
            throw e.appName(appName).environment(environment).country(country)
        }
    }

    private class BuilderBinding extends Binding {
        def builder

        Object getVariable(String name) {
            return { Object... args -> builder.invokeMethod(name, args) }
        }

    }
}
