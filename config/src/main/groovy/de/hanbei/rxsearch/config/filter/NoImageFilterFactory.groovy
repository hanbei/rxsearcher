package de.hanbei.rxsearch.config.filter

import de.hanbei.rxsearch.filter.impl.NoImageFilter


class NoImageFilterFactory extends AbstractFilterFactory {
    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        return new NoImageFilter()
    }
}
