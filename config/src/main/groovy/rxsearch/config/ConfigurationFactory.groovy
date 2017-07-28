package rxsearch.config

class ConfigurationFactory extends AbstractFactory {

    private static final String COUNTRY = "country"

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        return new Configuration(attributes.get(COUNTRY) as String)
    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        return false;
    }

    @Override
    boolean isLeaf() {
        return false;
    }
}