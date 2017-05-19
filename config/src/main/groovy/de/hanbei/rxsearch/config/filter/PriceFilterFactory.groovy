package de.hanbei.rxsearch.config.filter

import de.hanbei.rxsearch.filter.impl.PriceFilter

class PriceFilterFactory extends AbstractFilterFactory {
    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        return new PriceFilter();
    }
}
