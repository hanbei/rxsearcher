package rxsearch.config

class FilterConfigurationFactory extends AbstractFactory {

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        return new FilterConfiguration()
    }

    public void setParent(FactoryBuilderSupport builder, Object parentNode, Object childNode) {
        parentNode.filter = childNode
    }

    @Override
    boolean isLeaf() {
        return false;
    }

}