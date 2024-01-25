package io.github.isagroup.models.featuretypes;

import java.util.Map;

import io.github.isagroup.models.Feature;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Automation extends Feature {
    private AutomationType automationType;

    public String toString() {
        return "Automation[name: " + name + ", valueType: " + valueType + ", defaultValue: " + defaultValue
                + ", value: " + value + ", automationType: " + automationType + "]";
    }

    @Override
    public Map<String, Object> serializeFeature() {
        Map<String, Object> featuresAttributes = featureAttributesMap();

        if (automationType != null) {
            featuresAttributes.put("automationType", automationType.toString());
        }
        return featuresAttributes;
    }
}
