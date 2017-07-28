package rxsearch.config

class SearcherConfigurationFactory extends AbstractFactory {

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        return new SearcherConfiguration()
    }

    public void setParent(FactoryBuilderSupport builder, Object parentNode, Object childNode) {
        parentNode.searcher = childNode
    }

    @Override
    boolean isLeaf() {
        return false;
    }
}