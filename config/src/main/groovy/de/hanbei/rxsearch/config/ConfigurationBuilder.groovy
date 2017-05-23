package de.hanbei.rxsearch.config

import de.hanbei.rxsearch.config.loader.ClasspathConfigurationLoader
import de.hanbei.rxsearch.config.loader.ConfigurationLoader
import okhttp3.OkHttpClient

class ConfigurationBuilder {

    private final ConfigurationLoader configurationLoader
    private final ConfigurationFactoryBuilder configurationFactoryBuilder

    ConfigurationBuilder(OkHttpClient httpClient) {
        this(httpClient, new ClasspathConfigurationLoader())
    }

    ConfigurationBuilder(OkHttpClient httpClient, ConfigurationLoader configurationLoader) {
        this.configurationFactoryBuilder = new ConfigurationFactoryBuilder(httpClient)
        this.configurationLoader = configurationLoader
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
