package de.hanbei.rxsearch.config

import spock.lang.Specification

class ConfigurationLoaderTest extends Specification {

    def configurationLoader = new ConfigurationLoader()

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
}
