package de.hanbei.rxsearch.config

import com.google.common.base.Charsets
import com.google.common.io.Resources


class ConfigurationLoader {

    def String load(String name, String environment, String country) {
        def url = Resources.getResource(name + "/" + environment + "/" + country + ".groovy")
        return Resources.toString(url, Charsets.UTF_8).trim()
    }
}
