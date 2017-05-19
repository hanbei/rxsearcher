package de.hanbei.rxsearch.config

import okhttp3.OkHttpClient

class ConfigurationBuilder {

    def configurationLoader
    private final ConfigurationFactoryBuilder configurationFactoryBuilder

    ConfigurationBuilder(OkHttpClient httpClient) {
        this.configurationLoader = new ConfigurationLoader()
        this.configurationFactoryBuilder = new ConfigurationFactoryBuilder(httpClient)
    }

    Configuration loadConfiguration(String appName, String environment, String country) {
        String configuration = configurationLoader.load(appName, environment, country)

        try {
            GroovyClassLoader loader = new GroovyClassLoader(getClass().classLoader)
            Class<?> dsl = loader.parseClass(configuration)
            Configuration config = configurationFactoryBuilder.build(dsl) as Configuration
            return config
        } catch (Exception e) {
            throw ConfigurationException.wrap(e).appName(appName).environment(environment).country(country)
        }
    }

}
