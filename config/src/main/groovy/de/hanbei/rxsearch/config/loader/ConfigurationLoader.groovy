package de.hanbei.rxsearch.config.loader

interface ConfigurationLoader {

    String load(String appName, String environment, String country)

}