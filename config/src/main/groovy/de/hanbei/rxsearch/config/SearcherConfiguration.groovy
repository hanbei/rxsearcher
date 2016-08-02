package de.hanbei.rxsearch.config

import com.ning.http.client.AsyncHttpClient

class SearcherConfiguration {

    def configurationLoader
    def searchConfigBuilder

    SearcherConfiguration(AsyncHttpClient asynHttpClient) {
        this.configurationLoader = new ConfigurationLoader()
        this.searchConfigBuilder = new SearchConfigBuilder(asynHttpClient)
    }

    def loadConfiguration(String name, String environment, String country) {
        String configuration = configurationLoader.load(name, environment, country)

        def instance = new GroovyClassLoader().parseClass(configuration).newInstance()
        instance.binding = new BuilderBinding(builder: searchConfigBuilder)
        def searchers = instance.run()

        return searchers?.searchers
    }

    private class BuilderBinding extends Binding {
        def builder

        Object getVariable(String name) {
            return { Object... args -> builder.invokeMethod(name, args) }
        }

    }
}
