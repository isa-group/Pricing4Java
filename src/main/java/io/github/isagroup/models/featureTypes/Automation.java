package io.github.isagroup.models.featuretypes;

import java.util.Map;

import io.github.isagroup.models.Feature;
import io.github.isagroup.models.FeatureType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Automation extends Feature {

    private AutomationType automationType;

    @Override
    public Map<String, Object> serializeFeature() {
        Map<String, Object> featuresAttributes = featureAttributesMap();

        featuresAttributes.put("type", FeatureType.AUTOMATION.toString());

        if (automationType != null) {
            featuresAttributes.put("automationType", automationType.toString());
        }
        return featuresAttributes;
    }

    public String toString() {
        return "Automation[name: " + name + ", valueType: " + valueType + ", defaultValue: " + defaultValue
                + ", value: " + value + ", automationType: " + automationType + "]";
    }
}
