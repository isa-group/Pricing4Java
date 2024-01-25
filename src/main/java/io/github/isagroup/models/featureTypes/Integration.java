package io.github.isagroup.models.featuretypes;

import java.util.List;
import java.util.Map;

import io.github.isagroup.models.Feature;
import io.github.isagroup.models.FeatureType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Integration extends Feature {
    private IntegrationType integrationType;
    private List<String> pricingUrls;

    @Override
    public Map<String, Object> serializeFeature() {
        Map<String, Object> attributes = featureAttributesMap();

        attributes.put("type", FeatureType.INTEGRATION.toString());

        if (integrationType != null) {
            attributes.put("integrationType", integrationType.toString());
        }

        if (pricingUrls != null && !pricingUrls.isEmpty()) {
            attributes.put("pricingUrls", pricingUrls);
        }
        return attributes;
    }

    @Override
    public String toString() {
        return "Integration[name: " + name + ", valueType: " + valueType + ", defaultValue: " + defaultValue
                + ", value: " + value + ", integrationType: " + integrationType + ", pricingUrls: " + pricingUrls + "]";
    }

}
