package rxsearch.config.loader

import rxsearch.config.ConfigurationException
import spock.lang.Specification

class ClasspathConfigurationLoaderTest extends Specification {

    def configurationLoader = new ClasspathConfigurationLoader()

    def "load resource from test / production"() {
        when:
        def content = configurationLoader.load("test", "production", "de")

        then:
        content == "test/production"
    }

    def "load resource from test / staging"() {
        when:
        def content = configurationLoader.load("test", "staging", "de")

        then:
        content == "test/staging"
    }

    def "load not existing configuration"() {
        when:
        configurationLoader.load("does", "not", "exist")

        then:
        def e = thrown(ConfigurationException)
        e.message == "config not found"
        e.cause instanceof IllegalArgumentException
        e.environment == "not"
        e.country == "exist"
        e.appName == "does"
    }

    def "load empty configuration"() {
        when:
        configurationLoader.load("test", "staging", "empty")

        then:
        def e = thrown(ConfigurationException)
        e.message == "config is empty"
        e.cause == null
        e.environment == "staging"
        e.country == "empty"
        e.appName == "test"
    }
}
