package io.github.isagroup.services.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public class SkipNullRepresenter extends Representer {

    public SkipNullRepresenter() {
        super(new DumperOptions());
    }

    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue,
            Tag customTag) {
        // if value of property is null, ignore it.
        if (propertyValue == null) {
            return null;
        } else {
            return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
        }
    }
}
