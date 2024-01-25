package io.github.isagroup.models.featuretypes;

import java.util.Map;

import io.github.isagroup.models.Feature;
import io.github.isagroup.models.FeatureType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Management extends Feature {

    @Override
    public Map<String, Object> serializeFeature() {

        Map<String, Object> attributes = featureAttributesMap();
        attributes.put("type", FeatureType.MANAGEMENT.toString());
        return attributes;
    }

    @Override
    public String toString() {
        return "Management[name: " + name + ", valueType: " + valueType + ", defaultValue: " + defaultValue
                + ", value: " + value + "]";
    }

}
