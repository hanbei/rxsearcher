package de.hanbei.rxsearch.config

import groovy.transform.Immutable
import groovy.transform.InheritConstructors
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Immutable
@Builder(builderStrategy = SimpleStrategy, prefix = "")
@InheritConstructors
class ConfigurationException extends RuntimeException {

    String appName
    String environment
    String country

    static def wrap(Exception e) {
        if (e instanceof ConfigurationException) return e else new ConfigurationException(e)
    }

}
