package de.hanbei.rxsearch.config

class ConfigBuilder extends BuilderSupport {

    @Override
    protected void setParent(Object parent, Object child) {
        println "parent $parent << child $child"
    }

    @Override
    protected Object createNode(Object name) {
        return createNode(name, null, null);
    }

    @Override
    protected Object createNode(Object name, Object value) {
        return createNode(name, null, value);
    }

    @Override
    protected Object createNode(Object name, Map attributes) {
        return createNode(name, attributes, null);
    }

    @Override
    protected Object createNode(Object name, Map attributes, Object value) {
        println "* node $name with args $attributes and value $value."
        return name;
    }
}
