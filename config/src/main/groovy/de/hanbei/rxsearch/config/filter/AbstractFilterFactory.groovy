package de.hanbei.rxsearch.config.filter

import de.hanbei.rxsearch.config.ConfigurationException
import okhttp3.OkHttpClient

abstract class AbstractFilterFactory extends AbstractFactory {

    AbstractFilterFactory() {
    }

    protected def checkAttributes(String filterName, Map attributes, String... attributesToCheck) {
        for (attr in attributesToCheck) {
            if (!attributes.containsKey(attr)) {
                throw new ConfigurationException("Missing config value '${attr}' for '${filterName}'")
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
        parentNode.filters.add(childNode)
    }
}
