package de.hanbei.rxsearch.config.search

import de.hanbei.rxsearch.config.ConfigurationException
import okhttp3.OkHttpClient

abstract class AbstractSearcherFactory extends AbstractFactory {

    protected static final String NAME = "name"
    protected final OkHttpClient httpClient

    AbstractSearcherFactory(OkHttpClient httpClient) {
        this.httpClient = httpClient
    }

    protected def checkAttributes(String searcherName, Map attributes, String... attributesToCheck) {
        for (attr in attributesToCheck) {
            if (!attributes.containsKey(attr)) {
                throw new ConfigurationException("Missing config value '${attr}' for '${searcherName}'")
            }
        }
    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        return false;
    }

    @Override
    boolean isLeaf() {
        return true;
    }

    @Override
    void setParent(FactoryBuilderSupport builder, Object parentNode, Object childNode) {
        parentNode.searchers.add(childNode)
    }
}
