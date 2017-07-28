package rxsearch.config.loader

import com.google.common.base.Charsets
import com.google.common.io.Resources
import rxsearch.config.ConfigurationException

class ClasspathConfigurationLoader implements ConfigurationLoader {

    @Override
    String load(String appName, String environment, String country) {
        try {
            def url = Resources.getResource("${appName}/${environment}/${country}.groovy")
            def content = Resources.toString(url, Charsets.UTF_8).trim()
            if (content.isEmpty()) {
                throw new ConfigurationException("config is empty").appName(appName).environment(environment).country(country)
            }
            return content
        } catch (IllegalArgumentException e) {
            throw new ConfigurationException("config not found", e).appName(appName).environment(environment).country(country)
        }
    }
}
