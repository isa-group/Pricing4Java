package io.github.isagroup.models.featuretypes;

import java.util.Map;

import io.github.isagroup.models.Feature;
import io.github.isagroup.models.FeatureType;

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

    public AutomationType getAutomationType() {
        return automationType;
    }

    public void setAutomationType(AutomationType automationType) {
        this.automationType = automationType;
    }

    @Override
    public String toString() {
        return "Automation[name: " + name + ", valueType: " + valueType + ", defaultValue: " + defaultValue
                + ", value: " + value + ", automationType: " + automationType + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((automationType == null) ? 0 : automationType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Automation other = (Automation) obj;
        if (automationType != other.automationType)
            return false;
        return true;
    }

}
